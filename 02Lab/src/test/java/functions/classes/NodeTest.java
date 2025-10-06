package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    @Test
    public void testNodeInitialization() {
        Node node = new Node(1.1, 2.2);
        assertEquals(1.1, node.x);
        assertEquals(2.2, node.y);
        assertNull(node.next);
        assertNull(node.prev);
    }

    @Test
    public void testNodeLinking() {
        Node first = new Node(1,1);
        Node second = new Node(2,2);
        first.next = second;
        second.prev = first;

        assertSame(second, first.next);
        assertSame(first, second.prev);
    }
}