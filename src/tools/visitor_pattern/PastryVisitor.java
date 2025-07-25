package tools.visitor_pattern;

public interface PastryVisitor {
    public abstract void visitBeignet(Beignet beignet);
    public abstract void visitCruller(Cruller cruller);
    public abstract void deleteBeignet(Beignet beignet);
    public abstract void deleteCruller(Cruller cruller);
}
