/* fp_test.c - this last version took next to 5 hours.*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <glib.h>
#include <libfprint-2/fprint.h>

/* ---------------- globals for callback serialization ---------------- */
static GMutex g_serial_mutex;
static guchar *g_serialized_buf = NULL;
static gsize g_serialized_len = 0;
static gboolean g_serialized_ready = FALSE;

/* helper base64 */
static char* base64_encode(const guchar* data, gsize len) { return g_base64_encode(data, len); }
static guchar* base64_decode(const char* base64, gsize* len) { return g_base64_decode(base64, len); }

/* ---------------- enrollment progress callback ----------------
   We serialize inside the callback when completed==5. That guarantees the FpPrint is valid.
*/
static void enroll_progress(FpDevice *dev, gint completed, FpPrint *print, gpointer user_data, GError *error) {
    (void)dev;
    if (error) {
        fprintf(stderr, "ERR [progress]: %s\n", error->message);
        return;
    }
    fprintf(stderr, "[enroll_progress] Stage %d/5 completed\n", completed);

    if (completed == 5 && print != NULL) {
        g_mutex_lock(&g_serial_mutex);

        /* free any previous buffer just in case */
        if (g_serialized_buf) { g_free(g_serialized_buf); g_serialized_buf = NULL; g_serialized_len = 0; g_serialized_ready = FALSE; }

        GError *ser_err = NULL;
        guint8 *buf = NULL;
        gsize len = 0;

        /* Try serializing right now while object is guaranteed valid */
        if (!fp_print_serialize(print, &buf, &len, &ser_err)) {
            fprintf(stderr, "WARN [progress] serialize failed in callback: %s\n", ser_err ? ser_err->message : "unknown");
            if (ser_err) g_error_free(ser_err);
            g_serialized_ready = FALSE;
        } else {
            /* store the serialized copy (caller will free) */
            g_serialized_buf = buf;
            g_serialized_len = len;
            g_serialized_ready = TRUE;
            fprintf(stderr, "[progress] serialized %zu bytes inside callback\n", (size_t)len);
        }
        g_mutex_unlock(&g_serial_mutex);
    }
}

/* ---------------- match callback (no change) ---------------- */
static void match_cb(FpDevice *dev, FpPrint *match, FpPrint *print, gpointer user_data, GError *error) {
    (void)dev; (void)match; (void)print;
    if (error) fprintf(stderr, "ERR [match_cb]: %s\n", error->message);
}

/* ---------------- ENROLL ---------------- */
static int cmd_enroll(void) {
    GError *error = NULL;
    FpContext *ctx = fp_context_new();
    if (!ctx) { fprintf(stderr, "ERR: cannot init context\n"); return 1; }

    GPtrArray *devices = fp_context_get_devices(ctx);
    if (!devices || devices->len == 0) { fprintf(stderr, "ERR: no devices\n"); g_object_unref(ctx); return 1; }

    FpDevice *dev = FP_DEVICE(g_ptr_array_index(devices, 0));
    g_object_ref(dev);

    if (!fp_device_open_sync(dev, NULL, &error)) {
        fprintf(stderr, "ERR: open: %s\n", error ? error->message : "unknown");
        if (error) g_error_free(error);
        g_object_unref(dev);
        g_object_unref(ctx);
        return 1;
    }

    fprintf(stderr, "[fp_test] Place finger to enroll...\n");

    /* Ensure callback globals cleared */
    g_mutex_lock(&g_serial_mutex);
    if (g_serialized_buf) { g_free(g_serialized_buf); g_serialized_buf = NULL; g_serialized_len = 0; }
    g_serialized_ready = FALSE;
    g_mutex_unlock(&g_serial_mutex);

    /* create a template_print because some drivers require it */
    FpPrint *template_print = fp_print_new(dev);
    FpPrint *enrolled = fp_device_enroll_sync(dev, template_print, NULL, enroll_progress, NULL, &error);

    if (template_print && G_IS_OBJECT(template_print)) g_object_unref(template_print);

    if (!enrolled) {
        fprintf(stderr, "ERR: enrollment failed: %s\n", error ? error->message : "unknown");
        if (error) g_error_free(error);
        if (fp_device_is_open(dev)) fp_device_close_sync(dev, NULL, NULL);
        g_object_unref(dev); g_object_unref(ctx);
        return 1;
    }

    /* enrolled may be invalid after return on some drivers; but we serialized in callback */
    /* wait briefly to ensure callback finished writing the global */
    g_usleep(100 * 1000); /* 100ms */

    /* Retrieve serialized buffer from callback */
    g_mutex_lock(&g_serial_mutex);
    gboolean ready = g_serialized_ready;
    guchar *buf = g_serialized_buf;
    gsize len = g_serialized_len;

    /* null out globals so caller owns them */
    g_serialized_buf = NULL;
    g_serialized_len = 0;
    g_serialized_ready = FALSE;
    g_mutex_unlock(&g_serial_mutex);

    if (!ready || buf == NULL || len == 0) {
        fprintf(stderr, "ERR: serialization not available after enrollment (driver freed print early)\n");
        if (fp_device_is_open(dev)) fp_device_close_sync(dev, NULL, NULL);
        if (G_IS_OBJECT(enrolled)) g_object_unref(enrolled);
        g_object_unref(dev); g_object_unref(ctx);
        return 1;
    }

    /* Save binary file */
    FILE *f = fopen("fingerprint.bin", "wb");
    if (f) {
        fwrite(buf, 1, len, f);
        fclose(f);
        fprintf(stderr, "[fp_test] Saved fingerprint.bin (%zu bytes)\n", (size_t)len);
    } else {
        fprintf(stderr, "WARN: could not open fingerprint.bin for write\n");
    }

    /* print base64 to stdout for Java */
    char *b64 = base64_encode(buf, len);
    printf("OK %s\n", b64);
    g_free(b64);

    /* free serialized buffer we took ownership of */
    g_free(buf);

    if (fp_device_is_open(dev)) fp_device_close_sync(dev, NULL, NULL);
    if (G_IS_OBJECT(enrolled)) g_object_unref(enrolled);
    if (G_IS_OBJECT(dev)) g_object_unref(dev);
    if (G_IS_OBJECT(ctx)) g_object_unref(ctx);

    return 0;
}

/* ---------------- VERIFY ---------------- */
static int cmd_verify(const char *b64template) {
    if (!b64template) { fprintf(stderr,"ERR: missing template\n"); return 1; }

    GError *error = NULL;
    FpContext *ctx = fp_context_new();
    if (!ctx) { fprintf(stderr,"ERR: cannot init ctx\n"); return 1; }

    GPtrArray *devices = fp_context_get_devices(ctx);
    if (!devices || devices->len == 0) { fprintf(stderr,"ERR: no devices\n"); g_object_unref(ctx); return 1; }

    FpDevice *dev = FP_DEVICE(g_ptr_array_index(devices,0)); g_object_ref(dev);

    if (!fp_device_open_sync(dev, NULL, &error)) { fprintf(stderr,"ERR: open: %s\n", error?error->message:"unknown"); if (error) g_error_free(error); g_object_unref(dev); g_object_unref(ctx); return 1; }

    gsize len=0; guchar *dec = base64_decode(b64template, &len);
    if (!dec) { fprintf(stderr,"ERR: base64 decode\n"); if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL); g_object_unref(dev); g_object_unref(ctx); return 1; }

    FpPrint *stored = fp_print_deserialize(dec, len, &error); g_free(dec);
    if (!stored) { fprintf(stderr,"ERR: deserialize: %s\n", error?error->message:"unknown"); if (error) g_error_free(error); if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL); g_object_unref(dev); g_object_unref(ctx); return 1; }

    fprintf(stderr, "[finger_helper] Place finger to verify...\n");
    gboolean match = FALSE; FpPrint *verify_result = NULL;
    fp_device_verify_sync(dev, stored, NULL, match_cb, NULL, &match, &verify_result, &error);

    if (error) {
        fprintf(stderr,"ERR: verify: %s\n", error->message);
        g_error_free(error);
        if (verify_result) g_object_unref(verify_result);
        g_object_unref(stored);
        if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL);
        g_object_unref(dev); g_object_unref(ctx);
        return 1;
    }

    printf(match ? "OK\n" : "FAIL\n");

    if (verify_result) g_object_unref(verify_result);
    g_object_unref(stored);
    if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL);
    g_object_unref(dev); g_object_unref(ctx);
    return match ? 0 : 2;
}

/* ---------------- IDENTIFY ----------------
    Note: we disable incompatible-pointer-types warnings here because of
    the way we pass matched_print and match back from the identify_sync call.
*/
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wincompatible-pointer-types"
static int cmd_identify(int argc, char *argv[]) {
    if (argc < 3) { fprintf(stderr,"ERR: identify needs at least one template\n"); return 1; }

    GError *error = NULL;
    FpContext *ctx = fp_context_new();
    if (!ctx) { fprintf(stderr,"ERR: cannot init ctx\n"); return 1; }

    GPtrArray *devices = fp_context_get_devices(ctx);
    if (!devices || devices->len == 0) { fprintf(stderr,"ERR: no devices\n"); g_object_unref(ctx); return 1; }

    FpDevice *dev = FP_DEVICE(g_ptr_array_index(devices,0)); g_object_ref(dev);
    if (!fp_device_open_sync(dev, NULL, &error)) { fprintf(stderr,"ERR: open: %s\n", error?error->message:"unknown"); if (error) g_error_free(error); g_object_unref(dev); g_object_unref(ctx); return 1; }

    GPtrArray *prints = g_ptr_array_new_with_free_func((GDestroyNotify)g_object_unref);
    for (int i = 2; i < argc; ++i) {
        gsize len = 0;
        guchar *dec = base64_decode(argv[i], &len);
        if (!dec) continue;
        FpPrint *p = fp_print_deserialize(dec, len, &error);
        g_free(dec);
        if (!p) {
            if (error) { fprintf(stderr,"WARN: skip deserialize: %s\n", error->message); g_error_free(error); }
            continue;
        }
        g_ptr_array_add(prints, p);
    }

    if (prints->len == 0) {
        fprintf(stderr,"ERR: no valid stored prints\n");
        if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL);
        g_ptr_array_free(prints, TRUE);
        g_object_unref(dev); g_object_unref(ctx);
        return 1;
    }

    fprintf(stderr, "[finger_helper] Place finger to identify...\n");

    FpPrint *matched_print = NULL;
    gboolean match = FALSE;

    /* pass matched_print then match (matches your header ordering) */
    fp_device_identify_sync(dev, prints, NULL, match_cb, NULL, (FpPrint **)&matched_print, (gboolean *)&match, &error);

    if (error) {
        fprintf(stderr,"ERR: identify: %s\n", error->message);
        g_error_free(error);
        if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL);
        if (matched_print) g_object_unref(matched_print);
        g_ptr_array_free(prints, TRUE);
        g_object_unref(dev); g_object_unref(ctx);
        return 1;
    }

    if (!match) {
        printf("NO_MATCH\n");
        if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL);
        if (matched_print) g_object_unref(matched_print);
        g_ptr_array_free(prints, TRUE);
        g_object_unref(dev); g_object_unref(ctx);
        return 2;
    }

    int matched_index = -1;
    for (guint i = 0; i < prints->len; ++i) {
        if (g_ptr_array_index(prints, i) == matched_print) { matched_index = (int)i; break; }
    }
    printf("OK %d\n", matched_index);

    if (fp_device_is_open(dev)) fp_device_close_sync(dev,NULL,NULL);
    if (matched_print) g_object_unref(matched_print);
    g_ptr_array_free(prints, TRUE);
    g_object_unref(dev); g_object_unref(ctx);
    return 0;
}
#pragma GCC diagnostic pop

/* main */
int main(int argc, char *argv[]) {
    /* init mutex */
    g_mutex_init(&g_serial_mutex);

    if (argc < 2) { fprintf(stderr,"Usage: %s [enroll|verify <base64>|identify <base64>...]\n", argv[0]); return 1; }
    if (strcmp(argv[1],"enroll")==0) return cmd_enroll();
    if (strcmp(argv[1],"verify")==0 && argc==3) return cmd_verify(argv[2]);
    if (strcmp(argv[1],"identify")==0) return cmd_identify(argc, argv);
    fprintf(stderr,"Usage: %s [enroll|verify <base64>|identify <base64>...]\n", argv[0]);
    return 1;
}
