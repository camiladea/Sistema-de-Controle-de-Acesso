#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fprint.h>
#include <glib.h>
#include <time.h>

/**
 * Simple helper binary for enrolling and verifying fingerprints via libfprint.
 * 
 * Compatible with both older and newer libfprint builds — 
 * handles absence of fp_init()/fp_exit() gracefully.
 */

// Base64 helpers (glib)
static char *base64_encode(const unsigned char *data, size_t len) {
    return (char *)g_base64_encode(data, len);
}

static unsigned char *base64_decode(const char *b64, size_t *out_len) {
    return g_base64_decode(b64, out_len);
}

// Logging helper
static void log_msg(const char *msg) {
    time_t now = time(NULL);
    char buf[32];
    strftime(buf, sizeof(buf), "%H:%M:%S", localtime(&now));
    fprintf(stderr, "[%s] %s\n", buf, msg);
}

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s [enroll|verify <base64_template>]\n", argv[0]);
        return 1;
    }

    log_msg("Starting fingerprint helper");

    // --- Optional init (wrapped to avoid crashes on missing symbol) ---
    int (*lib_fp_init)(void) = dlsym(RTLD_DEFAULT, "fp_init");
    if (lib_fp_init) {
        log_msg("Calling fp_init()");
        lib_fp_init();
    } else {
        log_msg("fp_init() not found, skipping explicit initialization");
    }

    // Create context
    struct fp_context *ctx = fp_context_new();
    if (!ctx) {
        fprintf(stderr, "Error: cannot create fp_context\n");
        return 1;
    }

    // List devices
    GPtrArray *devices = fp_context_get_devices(ctx);
    if (!devices || devices->len == 0) {
        fprintf(stderr, "No fingerprint readers detected.\n");
        fp_context_free(ctx);
        return 1;
    }

    struct fp_device *dev = g_ptr_array_index(devices, 0);
    fprintf(stderr, "Using device: %s (%s)\n",
            fp_device_get_name(dev),
            fp_device_get_driver(dev));

    if (fp_device_open_sync(dev, NULL) != 0) {
        fprintf(stderr, "Failed to open device.\n");
        fp_context_free(ctx);
        return 1;
    }

    // --- ENROLL ---
    if (strcmp(argv[1], "enroll") == 0) {
        log_msg("Starting enrollment...");
        struct fp_print *print = fp_device_enroll_sync(dev, NULL, NULL);
        if (print) {
            gsize len = 0;
            g_autofree guint8 *bytes = fp_print_serialize(print, &len);
            if (bytes && len > 0) {
                char *b64 = base64_encode(bytes, len);
                printf("OK %s\n", b64);
                fprintf(stderr, "Enrollment OK. Template size: %lu bytes\n", len);
                g_free(b64);
            } else {
                fprintf(stderr, "Error: fp_print_serialize() failed.\n");
            }
            fp_print_free(print);
        } else {
            fprintf(stderr, "Enrollment failed (no print captured).\n");
        }
    }

    // --- VERIFY ---
    else if (strcmp(argv[1], "verify") == 0 && argc >= 3) {
        log_msg("Starting verification...");
        size_t tmpl_len = 0;
        unsigned char *tmpl = base64_decode(argv[2], &tmpl_len);
        struct fp_print *stored = fp_print_deserialize(tmpl, tmpl_len);
        g_free(tmpl);

        struct fp_print *captured = fp_device_capture_sync(dev, NULL, NULL);
        if (captured && stored) {
            gboolean match = fp_print_equal(stored, captured);
            printf("%s\n", match ? "OK" : "FAIL");
            fprintf(stderr, "Verification %s.\n", match ? "matched" : "failed");
        } else {
            fprintf(stderr, "Error capturing or deserializing print.\n");
        }
        if (stored) fp_print_free(stored);
        if (captured) fp_print_free(captured);
    }

    // --- UNKNOWN COMMAND ---
    else {
        fprintf(stderr, "Invalid command. Use: enroll | verify <template>\n");
    }

    fp_device_close_sync(dev, NULL);
    fp_context_free(ctx);

    // --- Optional exit (wrapped) ---
    void (*lib_fp_exit)(void) = dlsym(RTLD_DEFAULT, "fp_exit");
    if (lib_fp_exit) {
        log_msg("Calling fp_exit()");
        lib_fp_exit();
    } else {
        log_msg("fp_exit() not found, skipping explicit shutdown");
    }

    log_msg("Fingerprint helper finished.");
    return 0;
}
