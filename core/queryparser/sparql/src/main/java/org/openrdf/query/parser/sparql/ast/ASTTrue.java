/* Generated By:JJTree: Do not edit this line. ASTTrue.java */

package org.openrdf.query.parser.sparql.ast;

public class ASTTrue extends ASTRDFValue {

	public ASTTrue(int id) {
		super(id);
	}

	public ASTTrue(SyntaxTreeBuilder p, int id) {
		super(p, id);
	}

	@Override
	public Object jjtAccept(SyntaxTreeBuilderVisitor visitor, Object data)
		throws VisitorException
	{
		return visitor.visit(this, data);
	}
}
