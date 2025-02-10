package org.demo.GUIdemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LearningModeCalendar extends JFrame {
    private LocalDate currentDate;
    private JPanel calendarPanel;

    public LearningModeCalendar() {
        // 设置窗口
        setTitle("Learning Mode Calendar");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 获取当前日期
        currentDate = LocalDate.now();

        // 创建年历面板
        calendarPanel = new JPanel(new GridLayout(0, 7));
        add(calendarPanel, BorderLayout.CENTER);

        // 生成年历
        generateCalendar(currentDate.getYear());
    }

    private void generateCalendar(int year) {
        // 获取每个月的天数
        for (int month = 1; month <= 12; month++) {
            // 添加月份标题
            addMonthTitle(month);

            // 获取当前月份的天数
            int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setFont(new Font("Arial", Font.PLAIN, 12));

                LocalDate date = LocalDate.of(year, month, day);
                dayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleDayClick(date);
                    }
                });

                // 设置按钮的样式
                if (date.isBefore(currentDate)) {
                    dayButton.setBackground(Color.LIGHT_GRAY);  // 过去的日期
                } else if (date.isEqual(currentDate)) {
                    dayButton.setBackground(Color.CYAN);  // 今天的日期
                } else {
                    dayButton.setBackground(Color.GREEN);  // 未来的日期
                }

                calendarPanel.add(dayButton);
            }
        }
    }

    private void addMonthTitle(int month) {
        String monthName = LocalDate.of(2021, month, 1).getMonth().name();
        JLabel monthLabel = new JLabel(monthName, SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthLabel.setBackground(Color.GRAY);
        monthLabel.setOpaque(true);
        calendarPanel.add(monthLabel);
    }

    private void handleDayClick(LocalDate date) {
        JPanel panel = new JPanel();

        if (date.isBefore(currentDate)) {
            // 过去的日期
            panel = createReviewPanel(date);
        } else if (date.isEqual(currentDate)) {
            // 今天
            panel = createObservePanel(date);
        } else {
            // 未来的日期
            panel = createPlanPanel(date);
        }

        // 弹出窗口
        JFrame dayFrame = new JFrame("Day Details");
        dayFrame.setSize(400, 300);
        dayFrame.setLocationRelativeTo(this);
        dayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dayFrame.add(panel);
        dayFrame.setVisible(true);
    }

    // 回顾面板
    private JPanel createReviewPanel(LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTextArea reviewArea = new JTextArea("Review your Learning Mode data for " + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        reviewArea.setEditable(false);
        panel.add(reviewArea, BorderLayout.CENTER);
        return panel;
    }

    // 观察面板
    private JPanel createObservePanel(LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTextArea observeArea = new JTextArea("Observe Learning Mode data for today (" + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + ")");
        observeArea.setEditable(false);
        panel.add(observeArea, BorderLayout.CENTER);
        return panel;
    }

    // 计划面板
    private JPanel createPlanPanel(LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTextArea planArea = new JTextArea("Write your plan for " + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        panel.add(planArea, BorderLayout.CENTER);
        JButton saveButton = new JButton("Save Plan");
        saveButton.addActionListener(e -> {
            String plan = planArea.getText();
            JOptionPane.showMessageDialog(this, "Your plan for " + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " is saved.");
        });
        panel.add(saveButton, BorderLayout.SOUTH);
        return panel;
    }

    public static void main(String[] args) {
        // 启动程序
        SwingUtilities.invokeLater(() -> {
            LearningModeCalendar calendar = new LearningModeCalendar();
            calendar.setVisible(true);
        });
    }
}
