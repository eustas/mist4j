package ru.eustas.mist4j;

import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.SIPUSH;

import java.io.IOException;
import java.io.Writer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Constant values and utilities extracted from {@link TemplateAsm}.
 * 
 * @author Klyuchnikow Eugene
 * @version 2008.10.20 - initial version
 */
public class TemplateAsmConst {
	/**
	 * Constructor method name.
	 */
	static final String INIT = "<init>";

	/**
	 * Constructor signature - void method, with char-array parameter.
	 */
	static final String INIT_WITH_CHARS = "([C)V";

	/**
	 * {@link IFastRenderer#getOut()} method name.
	 */
	static final String GET_OUT = "getOut";

	/**
	 * {@link Writer#write(String, int, int)} method signature.
	 */
	static final String WRITE_PARAMS = "([CII)V";

	/**
	 * void parameterless method signature - for render methods.
	 */
	static final String VOID_METHOD = "()V";

	/**
	 * {@link IFastRenderer#getOut()} method name signature.
	 */
	static final String RETURN_WRITER_METHOD = "()Ljava/io/Writer;";

	/**
	 * Char-array type.
	 */
	static final String CHAR_ARRAY = "[C";

	/**
	 * "literal" field name.
	 */
	static final String FIELD_LITERAL = "literal";

	/**
	 * {@link ITemplate#process(IFastRenderer)} victim parameter - local
	 * variable - number.
	 */
	static final int PARAM_VICTIM = 1;

	/**
	 * Constructor parameter - local variable - number.
	 */
	static final int PARAM_SRC = 1;

	/**
	 * {@link ITemplate#process(IFastRenderer)} casted victim - local variable -
	 * number.
	 */
	static final int LOCAL_VICTIM = 1;

	/**
	 * {@link ITemplate#process(IFastRenderer)} {@link Writer} - local variable
	 * - number.
	 */
	static final int LOCAL_OUT = 2;

	/**
	 * {@link ITemplate#process(IFastRenderer)} literal char-array - local
	 * variable - number.
	 */
	static final int LOCAL_CHARS = 0;

	/**
	 * "this" - local variable - number.
	 */
	static final int THIS_REF = 0;

	/**
	 * {@link Writer#write(String, int, int)} method name.
	 */
	static final String WRITE = "write";

	/**
	 * "this" local variable name.
	 */
	static final String THIS = "this";

	/**
	 * {@link IFastRenderer} internal class name.
	 */
	static final String IFASTRENDERER = Type.getType(IFastRenderer.class)
			.getInternalName();

	/**
	 * {@link ITemplate} internal class name.
	 */
	static final String ITEMPLATE = Type.getType(ITemplate.class)
			.getInternalName();

	/**
	 * {@link Object} internal class name.
	 */
	final static String OBJECT = Type.getType(Object.class).getInternalName();

	/**
	 * {@link Writer} internal class name.
	 */
	final static String WRITER = Type.getType(Writer.class).getInternalName();

	/**
	 * {@link IOException} internal class name.
	 */
	final static String IOEXCEPTION = Type.getType(IOException.class)
			.getInternalName();;

	final static class Utils {
		/**
		 * Construct L-form of class.
		 * 
		 * @param cls
		 *            internal class name
		 * @return "L<i>cls</i>;"
		 */
		final static String l(String cls) {
			return "L" + cls + ";";
		}

		/**
		 * Write optimal byte codes for "push integer constant on stack".
		 * 
		 * @param mv
		 *            target
		 * @param i
		 *            integer constant
		 */
		final static void pushInt(MethodVisitor mv, int i) {
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
}