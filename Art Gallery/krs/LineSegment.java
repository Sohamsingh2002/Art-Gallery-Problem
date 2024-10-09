package krs;

public class LineSegment implements Comparable<LineSegment> {

    java.awt.Point a;
    java.awt.Point b;
    double l;

    public LineSegment(java.awt.Point p, java.awt.Point q) {
        a = p;
        b = q;
        l = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public int compareTo(LineSegment o) {
        if (this.l > o.l) {
            return 1;
        } else if (this.l < o.l) {
            return -1;
        }
        return 0;
    }

    public boolean equals(LineSegment o) {
        return this.l == o.l;
    }

    public String toString() {
        return a + " " + b;
    }
}
