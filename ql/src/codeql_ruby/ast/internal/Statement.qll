private import codeql_ruby.AST
private import codeql_ruby.ast.internal.TreeSitter

module Stmt {
  abstract class Range extends AstNode { }
}

module ReturningStmt {
  abstract class Range extends Stmt::Range {
    abstract Generated::ArgumentList getArgumentList();
  }
}

module ReturnStmt {
  class Range extends ReturningStmt::Range, @return {
    final override Generated::Return generated;

    final override string toString() { result = "return" }

    final override Generated::ArgumentList getArgumentList() { result = generated.getChild() }
  }
}

module BreakStmt {
  class Range extends ReturningStmt::Range, @break {
    final override Generated::Break generated;

    final override string toString() { result = "break" }

    final override Generated::ArgumentList getArgumentList() { result = generated.getChild() }
  }
}

module NextStmt {
  class Range extends ReturningStmt::Range, @next {
    final override Generated::Next generated;

    final override string toString() { result = "next" }

    final override Generated::ArgumentList getArgumentList() { result = generated.getChild() }
  }
}
