package ru.eustas.mist4j;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ru.eustas.mist4j.TemplateData.Range;

public class TemplateAsm implements Opcodes {

	private static final String INIT = "<init>";
	private static final String INIT_WITH_CHARS = "([C)V";
	private static final String GET_OUT = "getOut";
	private static final String WRITE_PARAMS = "([CII)V";
	private static final String VOID_METHOD = "()V";
	private static final String RETURN_WRITER_METHOD = "()Ljava/io/Writer;";
	private static final String CHAR_ARRAY = "[C";
	private static final String FIELD_LITERAL = "literal";
	private static final int PARAM_VICTIM = 1;
	private static final int PARAM_SRC = 1;
	private static final int LOCAL_VICTIM = 1;
	private static final int LOCAL_OUT = 2;
	private static final int LOCAL_CHARS = 0;
	private static final int THIS_REF = 0;
	private static final String WRITE = "write";
	private static final String THIS = "this";
	private static final String IFASTRENDERER = Type.getType(
			IFastRenderer.class).getInternalName();
	private static final String ITEMPLATE = Type.getType(ITemplate.class)
			.getInternalName();;
	private final static String OBJECT = "java/lang/Object";
	private final static String WRITER = "java/io/Writer";
	private final static String IOEXCEPTION = "java/io/IOException";

	private final static String l(String cls) {
		return "L" + cls + ";";
	}

	public static byte[] dump(String genCls, String victimCls, Range[] ranges,
			String[] invokers) {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;

		// HEADER
		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, genCls, null, OBJECT,
				new String[] { ITEMPLATE });
		cw.visitSource(genCls, null);

		// FIELDS
		// literal variable
		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, FIELD_LITERAL, CHAR_ARRAY,
				null, null);
		fv.visitEnd();

		// CONSTRUCTORS
		// constructor header
		mv = cw.visitMethod(ACC_PUBLIC, INIT, INIT_WITH_CHARS, null, null);
		mv.visitCode();
		Label labelStart = new Label();
		mv.visitLabel(labelStart);
		// | call Object.super
		mv.visitVarInsn(ALOAD, THIS_REF);
		mv.visitMethodInsn(INVOKESPECIAL, OBJECT, INIT, VOID_METHOD);
		// | literal := src
		mv.visitVarInsn(ALOAD, THIS_REF);
		mv.visitVarInsn(ALOAD, PARAM_SRC);
		mv.visitFieldInsn(PUTFIELD, genCls, FIELD_LITERAL, CHAR_ARRAY);
		// | return
		mv.visitInsn(RETURN);
		// constructor-meta
		Label labelEnd = new Label();
		mv.visitLabel(labelEnd);
		mv.visitLocalVariable(THIS, l(genCls), null, labelStart, labelEnd,
				THIS_REF);
		mv.visitLocalVariable("src", CHAR_ARRAY, null, labelStart, labelEnd,
				PARAM_SRC);
		mv.visitMaxs(2, 2);
		mv.visitEnd();

		// METHOD
		// header
		mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "process", "("
				+ l(IFASTRENDERER) + ")V", null, new String[] { IOEXCEPTION });
		mv.visitCode();
		labelStart = new Label();
		mv.visitLabel(labelStart);

		// get plain victim
		mv.visitVarInsn(ALOAD, PARAM_VICTIM);

		Label labelVicFree = new Label();
		mv.visitLabel(labelVicFree);

		// plain victim now free
		mv.visitTypeInsn(CHECKCAST, victimCls);
		// duplicate cast result
		mv.visitInsn(DUP);
		// one to save
		mv.visitVarInsn(ASTORE, LOCAL_VICTIM);
		// other is used to get writer
		// | call _vic.getOut
		mv.visitMethodInsn(INVOKEVIRTUAL, victimCls, GET_OUT,
				RETURN_WRITER_METHOD);
		// save writer
		// | _out := call _vic.getOut result
		mv.visitVarInsn(ASTORE, LOCAL_OUT);
		// get this
		mv.visitVarInsn(ALOAD, THIS_REF);

		Label labelThisFree = new Label();
		mv.visitLabel(labelThisFree);

		// this now free
		// | this.literal
		mv.visitFieldInsn(GETFIELD, genCls, FIELD_LITERAL, CHAR_ARRAY);
		// | _lit=this.literal
		mv.visitVarInsn(ASTORE, LOCAL_CHARS);

		// main cycle
		int l = ranges.length;
		for (int i = 0; i < l - 1; i++) {
			pushLiteral(mv, ranges[i]);
			mv.visitVarInsn(ALOAD, LOCAL_VICTIM);
			mv.visitMethodInsn(INVOKEVIRTUAL, victimCls, invokers[i],
					VOID_METHOD);
		}
		pushLiteral(mv, ranges[l - 1]);

		// | return
		mv.visitInsn(RETURN);
		// method-meta
		labelEnd = new Label();
		mv.visitLabel(labelEnd);
		// 0
		mv.visitLocalVariable(THIS, l(genCls), null, labelStart, labelThisFree,
				THIS_REF);
		mv.visitLocalVariable("_lit", CHAR_ARRAY, null, labelThisFree,
				labelEnd, LOCAL_CHARS);
		// 1
		mv.visitLocalVariable("vic", l(OBJECT), null, labelStart, labelVicFree,
				PARAM_VICTIM);
		mv.visitLocalVariable("_vic", l(victimCls), null, labelVicFree,
				labelEnd, LOCAL_VICTIM);
		// 2
		mv.visitLocalVariable("_out", l(WRITER), null, labelVicFree, labelEnd,
				LOCAL_OUT);
		mv.visitMaxs(4, 3);
		mv.visitEnd();
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void pushLiteral(MethodVisitor mv, Range range) {
		int len = range.getLen();
		if (len == 0) {
			return;
		}
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		pushInt(mv, range.getStart());
		pushInt(mv, len);
		mv.visitMethodInsn(INVOKEVIRTUAL, WRITER, WRITE, WRITE_PARAMS);
	}

	private static void pushInt(MethodVisitor mv, int i) {
		if (i < 0) {
			throw new IllegalArgumentException();
		}
		if (i == 0) {
			mv.visitInsn(ICONST_0);
		} else if (i == 1) {
			mv.visitInsn(ICONST_1);
		} else if (i == 2) {
			mv.visitInsn(ICONST_2);
		} else if (i == 3) {
			mv.visitInsn(ICONST_3);
		} else if (i == 4) {
			mv.visitInsn(ICONST_4);
		} else if (i == 5) {
			mv.visitInsn(ICONST_5);
		} else if (i <= 127) {
			mv.visitIntInsn(BIPUSH, i);
		} else if (i <= 32767) {
			mv.visitIntInsn(SIPUSH, i);
		} else {
			mv.visitLdcInsn(Integer.valueOf(i));
		}
	}
}