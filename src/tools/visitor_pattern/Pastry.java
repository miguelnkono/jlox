package tools.visitor_pattern;

abstract public class Pastry {
    public abstract void accept(PastryVisitor visitor);
    public abstract void delete(PastryVisitor visitor);
}
