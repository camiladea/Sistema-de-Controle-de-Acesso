package util;

import com.sun.jna.*;
import com.sun.jna.ptr.*;

public interface Libfprint extends Library {
    Libfprint INSTANCE = Native.load("fprint-2", Libfprint.class);

    Pointer fp_context_new();
    void fp_context_unref(Pointer ctx);

    Pointer fp_context_get_devices(Pointer ctx);
    void g_list_free(Pointer list);

    Pointer fp_device_open_sync(Pointer dev, PointerByReference error);
    void fp_device_close(Pointer dev);

    int fp_device_enroll_sync(Pointer dev, PointerByReference print, PointerByReference error);
    int fp_device_verify_sync(Pointer dev, Pointer print, PointerByReference result, PointerByReference error);

    Pointer fp_print_serialize(Pointer print, IntByReference size);
    Pointer fp_print_deserialize(Pointer ctx, Pointer data, int size, PointerByReference error);
    void g_object_unref(Pointer obj);
}
