package com.tookbra.water.order.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.tookbra.water.order.service.OrderThread;
import com.tookbra.water.order.service.TicketService;
import com.tookbra.water.order.utils.Config;
import com.tookbra.water.order.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by tookbra on 2017/8/7.
 */
public class MainFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private JFrame mainJFrame;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JLabel versionLabel;
    private JLabel checkUpdateLabel;
    private JTable shipTable;
    private JComboBox shipComboBox;
    private JButton ticketSearchButton;
    private JTextField timeTxt;
    private JTextField timeSettingTxt;
    private JComboBox shipSettingComboBox;
    private JCheckBox a0750CheckBox;
    private JCheckBox a0840CheckBox;
    private JCheckBox a0910CheckBox;
    private JCheckBox a0940CheckBox;
    private JCheckBox a0950CheckBox;
    private JCheckBox a1250CheckBox1;
    private JCheckBox a1300CheckBox1;
    private JCheckBox a1330CheckBox1;
    private JCheckBox a1340CheckBox1;
    private JCheckBox a1430CheckBox1;
    private JCheckBox a1440CheckBox1;
    private JButton settingButton;
    private JPanel settingPanel;
    private JButton startRunButton;
    private JTextArea logTextArea;
    private JCheckBox a1510CheckBox1;
    private JCheckBox a1530CheckBox1;
    private JCheckBox a1550CheckBox1;
    private JCheckBox a1600CheckBox1;
    private JCheckBox a1610CheckBox;
    private JCheckBox a1620CheckBox;
    private JCheckBox a1640CheckBox;
    private JCheckBox a1700CheckBox;
    private JCheckBox a1710CheckBox1;
    private JCheckBox a1000CheckBox1;
    private JCheckBox a1030CheckBox1;
    private JCheckBox a1050CheckBox1;
    private JCheckBox a1210CheckBox1;
    private JCheckBox a1220CheckBox1;
    private JTextField orderNumTxt;
    private JRadioButton topRadioButton;
    private JRadioButton middleRadioButton;
    private JRadioButton footerRadioButton;
    private JCheckBox a1920CheckBox1;
    private JCheckBox a1010CheckBox;
    private JCheckBox a1035CheckBox;
    private JCheckBox a1320CheckBox;
    private JCheckBox a1400CheckBox;
    private ButtonGroup berthGroup;
    public static MainFrame mainFrame;
    public boolean runFlag = true;
    public static ScheduledExecutorService executorService;
    public static ScheduledExecutorService orderService = Executors.newSingleThreadScheduledExecutor();

    public MainFrame() {
        mainFrame = this;
        String[] headerNames = {"开航时间", "船舶类型", "船名", "舱位1", "舱位2", "舱位3"};

        DefaultTableModel model = new DefaultTableModel(null, headerNames);
        shipTable.setModel(model);
        shipTable.updateUI();

        timeTxt.setText(DateUtil.getDay());


        timeSettingTxt.setText(Config.getInstance().getSechedultTime());
        orderNumTxt.setText(Config.getInstance().getOrderNumTotal().toString());
        List<String> orderShipList = Config.getInstance().getOrderShip();
        Component[] components = settingPanel.getComponents();
        for (String orderShip : orderShipList) {
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.getText().equals(orderShip)) {
                        checkBox.setSelected(true);
                    } else {
                        checkBox.setSelected(false);
                    }
                }
            }
        }
        if (Config.getInstance().getTopBerth()) {
            topRadioButton.setSelected(true);
        } else {
            topRadioButton.setSelected(false);
        }

        if (Config.getInstance().getMiddleBerth()) {
            middleRadioButton.setSelected(true);
        } else {
            middleRadioButton.setSelected(false);
        }

        if (Config.getInstance().getBottomBerth()) {
            footerRadioButton.setSelected(true);
        } else {
            footerRadioButton.setSelected(false);
        }


        ticketSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (StringUtils.isBlank(timeTxt.getText())) {
                    JOptionPane.showMessageDialog(mainJFrame, "请输入查询日期", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String ship = shipComboBox.getSelectedItem().toString();

                DefaultTableModel tableModel = (DefaultTableModel) shipTable.getModel();
                tableModel.setRowCount(0);


                List<String[]> ticketList = TicketService.instance.ticketSearch(ship, timeTxt.getText().trim());
                if (!ticketList.isEmpty()) {
                    for (String[] ticketArray : ticketList) {
                        tableModel.addRow(ticketArray);
                    }
                }
                shipTable.invalidate();
            }
        });

        shipSettingComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println(e.getItem());
                }
            }
        });


        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String time = timeSettingTxt.getText().trim();
                    if (StringUtils.isBlank(time)) {
                        JOptionPane.showMessageDialog(mainJFrame, "请设置抢票开始时间", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (!DateUtil.checkTimeFormat(time)) {
                        JOptionPane.showMessageDialog(mainJFrame, "抢票开始时间格式错误(HH:mm:ss)", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String orderNumTotal = orderNumTxt.getText().trim();
                    if (StringUtils.isBlank(orderNumTotal)) {
                        JOptionPane.showMessageDialog(mainJFrame, "请设置限购张数", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (!topRadioButton.isSelected()
                            && !middleRadioButton.isSelected()
                            && !footerRadioButton.isSelected()) {
                        JOptionPane.showMessageDialog(mainJFrame, "请选中舱位优先", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (topRadioButton.isSelected()) {
                        Config.getInstance().setTopBerth(true);
                    } else if (middleRadioButton.isSelected()) {
                        Config.getInstance().setMiddleBerth(true);
                    } else if (footerRadioButton.isSelected()) {
                        Config.getInstance().setBottomBerth(true);
                    }

                    String ship = shipComboBox.getSelectedItem().toString();

                    final Component[] components = settingPanel.getComponents();
                    List<String> list = new ArrayList<>();
                    for (Component component : components) {
                        if (component instanceof JCheckBox) {
                            JCheckBox checkBox = (JCheckBox) component;
                            if (checkBox.isSelected()) {
                                list.add(checkBox.getText());
                            }
                        }
                    }


                    Config.getInstance().setSecheduleTime(time);
                    Config.getInstance().setShip(ship);
                    Config.getInstance().setOrderNumTotal(orderNumTotal);
                    Config.getInstance().setOrderShip(list);
                    Config.getInstance().save();

                    JOptionPane.showMessageDialog(mainJFrame, "保存设置成功,如有抢票任务在运行将停止", "提示", JOptionPane.INFORMATION_MESSAGE);
                    stopRun();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    JOptionPane.showMessageDialog(mainJFrame, "保存设置失败", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        startRunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (preCheck()
                                && Config.getInstance().getOrderNum() != Config.getInstance().getOrderNumTotal()) {
                            int isPush = JOptionPane.showConfirmDialog(mainJFrame,
                                    startRunButton.getText().equals("停止抢票") ? "确定停止抢票吗？" : "确定开始抢票吗？",
                                    "抢票？",
                                    JOptionPane.INFORMATION_MESSAGE);
                            if (isPush == JOptionPane.YES_OPTION) {
                                // 按钮状态
                                if (startRunButton.getText().equals("停止抢票")) {
                                    startRunButton.setText("开始抢票");
                                    executorService.shutdown();
                                } else {
                                    startRunButton.setText("停止抢票");
                                    if (DateUtil.compore(DateUtil.getLocalTime(),
                                            DateUtil.getLocalTimeByFmt(Config.getInstance().getSechedultTime()))) {
                                        executorService = Executors.newSingleThreadScheduledExecutor();
                                        executorService.scheduleWithFixedDelay(new OrderThread(), 1000, 3000, TimeUnit.MILLISECONDS);
                                    }

                                }
                            }
                        }
                    }
                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                long startPerDayMills = DateUtil.getLocalDateTimeByFmt(DateUtil.getDay() + " 01:00:00");
                long millisBetween = startPerDayMills - System.currentTimeMillis();
                long delay = millisBetween < 0 ? millisBetween + 24 * 60 * 60 * 1000 : millisBetween;
                orderService.scheduleAtFixedRate(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Config.getInstance().setOrderNum("0");
                    }
                }), delay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
            }
        }).start();


        mainJFrame = new JFrame("水上客运抢票");
        mainJFrame.setContentPane(mainPanel);
        mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainJFrame.setResizable(false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸
        Dimension preferSize = new Dimension((int) (screenSize.width * 0.6),
                (int) (screenSize.height * 0.6));
        mainJFrame.setPreferredSize(preferSize);
        mainJFrame.setBounds((int) (screenSize.width - preferSize.width) / 2, (int) (screenSize.height - preferSize.height) / 2, (int) (screenSize.width * 0.8),
                (int) (screenSize.height * 0.8));

        mainJFrame.pack();
        mainJFrame.setVisible(true);
    }

    private void stopRun() {
        if (executorService != null && !executorService.isShutdown()) {
            startRunButton.setText("开始抢票");
            executorService.shutdownNow();
        }
    }

    public JTable getShipTable() {
        return shipTable;
    }

    public MainFrame setShipTable(JTable shipTable) {
        this.shipTable = shipTable;
        return this;
    }

    private boolean preCheck() {
        if (StringUtils.isBlank(Config.getInstance().getSechedultTime())) {
            JOptionPane.showMessageDialog(mainJFrame, "请设置抢票开始时间", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (Config.getInstance().getOrderShip().isEmpty()) {
            JOptionPane.showMessageDialog(mainJFrame, "请设置抢票航班", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (Config.getInstance().getOrderNumTotal() == null) {
            JOptionPane.showMessageDialog(mainJFrame, "请设置抢票数量", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    public MainFrame setLogTextArea(JTextArea logTextArea) {
        this.logTextArea = logTextArea;
        return this;
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPane.addTab("余票查询", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        panel2.setBorder(BorderFactory.createTitledBorder("查询条件"));
        final JLabel label1 = new JLabel();
        label1.setText("航线");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shipComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("(进嵊)沈家湾--泗礁");
        defaultComboBoxModel1.addElement("(出嵊)泗礁--沈家湾--南浦");
        defaultComboBoxModel1.addElement("(进枸)沈家湾--枸杞");
        shipComboBox.setModel(defaultComboBoxModel1);
        panel2.add(shipComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("日期");
        panel2.add(label2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeTxt = new JTextField();
        panel2.add(timeTxt, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ticketSearchButton = new JButton();
        ticketSearchButton.setEnabled(true);
        ticketSearchButton.setText("查询");
        panel2.add(ticketSearchButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(1, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("查询结果"));
        shipTable = new JTable();
        shipTable.setAutoCreateColumnsFromModel(true);
        shipTable.setAutoCreateRowSorter(true);
        shipTable.setGridColor(new Color(-12236470));
        shipTable.setRowHeight(30);
        panel3.add(shipTable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("船票预定", panel4);
        startRunButton = new JButton();
        startRunButton.setText("开始抢票");
        panel4.add(startRunButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logTextArea = new JTextArea();
        logTextArea.setLineWrap(true);
        panel4.add(logTextArea, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        settingPanel = new JPanel();
        settingPanel.setLayout(new GridLayoutManager(10, 7, new Insets(0, 0, 0, 0), -1, -1));
        settingPanel.setName("设置");
        settingPanel.setOpaque(false);
        settingPanel.setVisible(true);
        tabbedPane.addTab("设置", settingPanel);
        final JLabel label3 = new JLabel();
        label3.setText("抢票时间");
        label3.setVisible(true);
        settingPanel.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeSettingTxt = new JTextField();
        timeSettingTxt.setText("07:30:00");
        settingPanel.add(timeSettingTxt, new GridConstraints(0, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("航线");
        settingPanel.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shipSettingComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("(进嵊)沈家湾--泗礁");
        shipSettingComboBox.setModel(defaultComboBoxModel2);
        settingPanel.add(shipSettingComboBox, new GridConstraints(3, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("开航时间(进嵊)");
        settingPanel.add(label5, new GridConstraints(4, 0, 5, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingButton = new JButton();
        settingButton.setText("保存");
        settingPanel.add(settingButton, new GridConstraints(9, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a0750CheckBox = new JCheckBox();
        a0750CheckBox.setText("07:50");
        settingPanel.add(a0750CheckBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a0840CheckBox = new JCheckBox();
        a0840CheckBox.setText("08:40");
        settingPanel.add(a0840CheckBox, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a0910CheckBox = new JCheckBox();
        a0910CheckBox.setText("09:10");
        settingPanel.add(a0910CheckBox, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a0940CheckBox = new JCheckBox();
        a0940CheckBox.setText("09:40");
        settingPanel.add(a0940CheckBox, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a0950CheckBox = new JCheckBox();
        a0950CheckBox.setText("09:50");
        settingPanel.add(a0950CheckBox, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1250CheckBox1 = new JCheckBox();
        a1250CheckBox1.setText("12:50");
        settingPanel.add(a1250CheckBox1, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1300CheckBox1 = new JCheckBox();
        a1300CheckBox1.setText("13:00");
        settingPanel.add(a1300CheckBox1, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1610CheckBox = new JCheckBox();
        a1610CheckBox.setText("16:10");
        settingPanel.add(a1610CheckBox, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1620CheckBox = new JCheckBox();
        a1620CheckBox.setText("16:20");
        settingPanel.add(a1620CheckBox, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1640CheckBox = new JCheckBox();
        a1640CheckBox.setText("16:40");
        settingPanel.add(a1640CheckBox, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1700CheckBox = new JCheckBox();
        a1700CheckBox.setText("17:00");
        settingPanel.add(a1700CheckBox, new GridConstraints(8, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1710CheckBox1 = new JCheckBox();
        a1710CheckBox1.setText("17:10");
        settingPanel.add(a1710CheckBox1, new GridConstraints(8, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1030CheckBox1 = new JCheckBox();
        a1030CheckBox1.setText("10:30");
        settingPanel.add(a1030CheckBox1, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("限购张数");
        settingPanel.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        orderNumTxt = new JTextField();
        orderNumTxt.setText("50");
        settingPanel.add(orderNumTxt, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("舱位优先");
        settingPanel.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        topRadioButton = new JRadioButton();
        topRadioButton.setName("");
        topRadioButton.setText("上舱");
        settingPanel.add(topRadioButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        middleRadioButton = new JRadioButton();
        middleRadioButton.setText("中舱");
        settingPanel.add(middleRadioButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        footerRadioButton = new JRadioButton();
        footerRadioButton.setSelected(true);
        footerRadioButton.setText("下舱");
        settingPanel.add(footerRadioButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1920CheckBox1 = new JCheckBox();
        a1920CheckBox1.setText("19:20");
        settingPanel.add(a1920CheckBox1, new GridConstraints(8, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1000CheckBox1 = new JCheckBox();
        a1000CheckBox1.setText("10:00");
        settingPanel.add(a1000CheckBox1, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1010CheckBox = new JCheckBox();
        a1010CheckBox.setText("10:10");
        settingPanel.add(a1010CheckBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1220CheckBox1 = new JCheckBox();
        a1220CheckBox1.setText("12:20");
        settingPanel.add(a1220CheckBox1, new GridConstraints(5, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1210CheckBox1 = new JCheckBox();
        a1210CheckBox1.setText("12:10");
        settingPanel.add(a1210CheckBox1, new GridConstraints(5, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1050CheckBox1 = new JCheckBox();
        a1050CheckBox1.setText("10:50");
        settingPanel.add(a1050CheckBox1, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1035CheckBox = new JCheckBox();
        a1035CheckBox.setText("10:35");
        settingPanel.add(a1035CheckBox, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1340CheckBox1 = new JCheckBox();
        a1340CheckBox1.setText("13:40");
        settingPanel.add(a1340CheckBox1, new GridConstraints(6, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1330CheckBox1 = new JCheckBox();
        a1330CheckBox1.setText("13:30");
        settingPanel.add(a1330CheckBox1, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1320CheckBox = new JCheckBox();
        a1320CheckBox.setText("13:20");
        settingPanel.add(a1320CheckBox, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1600CheckBox1 = new JCheckBox();
        a1600CheckBox1.setText("16:00");
        settingPanel.add(a1600CheckBox1, new GridConstraints(7, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1550CheckBox1 = new JCheckBox();
        a1550CheckBox1.setText("15:50");
        settingPanel.add(a1550CheckBox1, new GridConstraints(7, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1530CheckBox1 = new JCheckBox();
        a1530CheckBox1.setText("15:30");
        settingPanel.add(a1530CheckBox1, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1510CheckBox1 = new JCheckBox();
        a1510CheckBox1.setText("15:10");
        settingPanel.add(a1510CheckBox1, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1440CheckBox1 = new JCheckBox();
        a1440CheckBox1.setText("14:40");
        settingPanel.add(a1440CheckBox1, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1430CheckBox1 = new JCheckBox();
        a1430CheckBox1.setText("14:30");
        settingPanel.add(a1430CheckBox1, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1400CheckBox = new JCheckBox();
        a1400CheckBox.setText("14:00");
        settingPanel.add(a1400CheckBox, new GridConstraints(6, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(5, 6, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPane.addTab("关于", panel5);
        final JLabel label8 = new JLabel();
        label8.setEnabled(true);
        label8.setFont(new Font(label8.getFont().getName(), label8.getFont().getStyle(), 36));
        label8.setText(" 水上客运自动购票");
        panel5.add(label8, new GridConstraints(0, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Proudly by 喵了个咪");
        panel5.add(label9, new GridConstraints(1, 0, 2, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        versionLabel = new JLabel();
        versionLabel.setFont(new Font("Microsoft YaHei UI", versionLabel.getFont().getStyle(), versionLabel.getFont().getSize()));
        versionLabel.setText("版本：v_0.0.1");
        panel5.add(versionLabel, new GridConstraints(3, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkUpdateLabel = new JLabel();
        checkUpdateLabel.setText("检查更新");
        panel5.add(checkUpdateLabel, new GridConstraints(4, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        berthGroup = new ButtonGroup();
        berthGroup.add(topRadioButton);
        berthGroup.add(middleRadioButton);
        berthGroup.add(footerRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
