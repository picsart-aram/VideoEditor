	.arch armv7-a
	.eabi_attribute 27, 3
	.fpu vfpv3-d16
	.eabi_attribute 20, 1
	.eabi_attribute 21, 1
	.eabi_attribute 23, 3
	.eabi_attribute 24, 1
	.eabi_attribute 25, 1
	.eabi_attribute 26, 2
	.eabi_attribute 30, 2
	.eabi_attribute 18, 4
	.file	"vpx_scale_asm_offsets.c"
	.section	.text.startup.main,"ax",%progbits
	.align	2
	.global	main
	.type	main, %function
main:
	.fnstart
	@ args = 0, pretend = 0, frame = 0
	@ frame_needed = 0, uses_anonymous_args = 0
	@ link register save eliminated.
#APP
@ 20 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_y_width EQU #0
@ 0 "" 2
@ 21 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_y_height EQU #4
@ 0 "" 2
@ 22 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_y_stride EQU #16
@ 0 "" 2
@ 23 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_uv_width EQU #20
@ 0 "" 2
@ 24 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_uv_height EQU #24
@ 0 "" 2
@ 25 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_uv_stride EQU #36
@ 0 "" 2
@ 26 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_y_buffer EQU #52
@ 0 "" 2
@ 27 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_u_buffer EQU #56
@ 0 "" 2
@ 28 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_v_buffer EQU #60
@ 0 "" 2
@ 29 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
yv12_buffer_config_border EQU #76
@ 0 "" 2
@ 30 "/Users/user/Downloads/videocodingtest/examples/bindingEncodeExample/jni/libvpx/vpx_scale/vpx_scale_asm_offsets.c" 1
	
VP8BORDERINPIXELS_VAL EQU #32
@ 0 "" 2
	mov	r0, #0
	bx	lr
	.fnend
	.size	main, .-main
	.ident	"GCC: (GNU) 4.6 20120106 (prerelease)"
	.section	.note.GNU-stack,"",%progbits
