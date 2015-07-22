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
	.file	"vp8_asm_enc_offsets.c"
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
@ 23 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_coeff EQU #4
@ 0 "" 2
@ 24 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_zbin EQU #20
@ 0 "" 2
@ 25 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_round EQU #28
@ 0 "" 2
@ 26 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_quant EQU #8
@ 0 "" 2
@ 27 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_quant_fast EQU #12
@ 0 "" 2
@ 28 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_zbin_extra EQU #32
@ 0 "" 2
@ 29 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_zrun_zbin_boost EQU #24
@ 0 "" 2
@ 30 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_quant_shift EQU #16
@ 0 "" 2
@ 32 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_blockd_qcoeff EQU #0
@ 0 "" 2
@ 33 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_blockd_dequant EQU #12
@ 0 "" 2
@ 34 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_blockd_dqcoeff EQU #4
@ 0 "" 2
@ 35 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_blockd_eob EQU #20
@ 0 "" 2
@ 38 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_base_src EQU #36
@ 0 "" 2
@ 39 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_src EQU #40
@ 0 "" 2
@ 40 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_src_diff EQU #0
@ 0 "" 2
@ 41 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_block_src_stride EQU #44
@ 0 "" 2
@ 43 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_blockd_predictor EQU #8
@ 0 "" 2
@ 46 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_lowvalue EQU #0
@ 0 "" 2
@ 47 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_range EQU #4
@ 0 "" 2
@ 48 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_count EQU #8
@ 0 "" 2
@ 49 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_pos EQU #12
@ 0 "" 2
@ 50 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_buffer EQU #16
@ 0 "" 2
@ 51 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_buffer_end EQU #20
@ 0 "" 2
@ 52 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_error EQU #24
@ 0 "" 2
@ 54 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
tokenextra_token EQU #6
@ 0 "" 2
@ 55 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
tokenextra_extra EQU #4
@ 0 "" 2
@ 56 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
tokenextra_context_tree EQU #0
@ 0 "" 2
@ 57 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
tokenextra_skip_eob_node EQU #7
@ 0 "" 2
@ 58 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
TOKENEXTRA_SZ EQU #8
@ 0 "" 2
@ 60 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_extra_bit_struct_sz EQU #16
@ 0 "" 2
@ 62 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_token_value EQU #0
@ 0 "" 2
@ 63 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_token_len EQU #4
@ 0 "" 2
@ 65 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_extra_bit_struct_tree EQU #0
@ 0 "" 2
@ 66 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_extra_bit_struct_prob EQU #4
@ 0 "" 2
@ 67 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_extra_bit_struct_len EQU #8
@ 0 "" 2
@ 68 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_extra_bit_struct_base_val EQU #12
@ 0 "" 2
@ 70 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_comp_tplist EQU #139872
@ 0 "" 2
@ 71 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_comp_common EQU #105664
@ 0 "" 2
@ 72 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_comp_bc EQU #110880
@ 0 "" 2
@ 73 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_writer_sz EQU #36
@ 0 "" 2
@ 75 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
tokenlist_start EQU #0
@ 0 "" 2
@ 76 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
tokenlist_stop EQU #4
@ 0 "" 2
@ 77 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
TOKENLIST_SZ EQU #8
@ 0 "" 2
@ 79 "/Users/vahagnnikoghosyan/picsart-android/picsart_video_encoder/jni/libvpx/vp8/encoder/vp8_asm_enc_offsets.c" 1
	
vp8_common_mb_rows EQU #2424
@ 0 "" 2
	mov	r0, #0
	bx	lr
	.fnend
	.size	main, .-main
	.ident	"GCC: (GNU) 4.6 20120106 (prerelease)"
	.section	.note.GNU-stack,"",%progbits
