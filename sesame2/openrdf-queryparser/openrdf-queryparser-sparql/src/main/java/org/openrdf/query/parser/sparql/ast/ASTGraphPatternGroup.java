/* Generated By:JJTree: Do not edit this line. ASTGraphPatternGroup.java */

package org.openrdf.query.parser.sparql.ast;

public class ASTGraphPatternGroup extends SimpleNode {

	public ASTGraphPatternGroup(int id) {
		super(id);
	}

	public ASTGraphPatternGroup(SyntaxTreeBuilder p, int id) {
		super(p, id);
	}

	@Override
	public Object jjtAccept(SyntaxTreeBuilderVisitor visitor, Object data)
		throws VisitorException
	{
		return visitor.visit(this, data);
	}
}
