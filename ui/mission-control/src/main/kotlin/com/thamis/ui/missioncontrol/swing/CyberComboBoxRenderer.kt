package com.thamis.ui.missioncontrol.swing

import com.thamis.ui.missioncontrol.ComboBoxItem
import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

/**
 * Custom Swing ListCellRenderer for styling JComboBox with THAMIS Lab Cyber Dark theme.
 */
public class CyberComboBoxRenderer : DefaultListCellRenderer() {

    private val bgDark = Color(14, 23, 34)
    private val bgSelected = Color(0, 136, 145)
    private val textCyan = Color(0, 242, 255)
    private val textWhite = Color(255, 255, 255)
    private val borderCyan = Color(0, 242, 255)

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JComponent

        c.font = Font("Consolas", Font.BOLD, 12)
        c.border = EmptyBorder(6, 12, 6, 12)

        val displayText = when (value) {
            is ComboBoxItem -> value.displayName
            is String -> value
            else -> value?.toString() ?: ""
        }

        text = displayText

        if (isSelected) {
            c.background = bgSelected
            c.foreground = textWhite
        } else {
            c.background = bgDark
            c.foreground = textCyan
        }

        return c
    }

    public companion object {
        public fun applyCyberStyle(comboBox: JComboBox<ComboBoxItem>) {
            comboBox.renderer = CyberComboBoxRenderer()
            comboBox.background = Color(14, 23, 34)
            comboBox.foreground = Color(0, 242, 255)
            comboBox.font = Font("Consolas", Font.BOLD, 12)
            comboBox.border = LineBorder(Color(0, 242, 255), 1, true)
            comboBox.isFocusable = false
        }
    }
}
