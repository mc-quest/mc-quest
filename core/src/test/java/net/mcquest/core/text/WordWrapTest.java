package net.mcquest.core.text;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordWrapTest {
    @Test
    public void testEmpty() {
        test(List.of(Component.empty()), "");
    }

    @Test
    public void testNewLine() {
        test(List.of(Component.empty(), Component.empty()), "\n");
    }

    private void test(List<Component> expected, String text) {
        assertEquals(expected, WordWrap.wrap(text));
    }
}
