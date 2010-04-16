/*
 * #%L
 * JRst :: Api
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2004 - 2010 CodeLutin
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.nuiton.jrst;

import static org.nuiton.i18n.I18n._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.nuiton.util.Resource;

/**
 * JRST main interface.
 *
 * @author chatellier
 * @version $Revision$
 * 
 * Last update : $Date$
 * By : $Author$
 */
public class JRSTInterface extends JDialog {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 5327326730753891936L;

    private JPanel savePanel;

    private JPanel formatPanel;

    private JPanel openPanel;

    private JPanel formatLigne;

    private JPanel xslLigne;

    private JComboBox formatList;

    private JButton boutonAnnuler;

    private JButton boutonConvertir;

    private JPanel panelPrincipal;

    private JPanel boutonPanel;

    private JButton boutonSaveLocation;

    private JLabel errorLbl;

    private JButton boutonOpenLocation;

    private JButton boutonXslLocation;

    private String[] listFormats;

    private JTextField saveText;

    private JTextField openText;

    private JTextField xslText;

    private JRadioButton format;

    private JRadioButton xslRadio;

    private LinkedList<JPanel> ListAddXslPanel;

    private LinkedList<JTextField> ListXslText;

    private LinkedList<JButton> ListXslBouton;

    private LinkedList<JButton> ListXslBoutonLocation;
    private boolean ecrase;
    private String[] commande;

    private ImageIcon open = Resource.getIcon("icone/open.png");
    private ImageIcon delete = Resource.getIcon("icone/cancel.png");
    private ImageIcon more = Resource.getIcon("icone/more.gif");

    private LinkedList<Container> composantsXSL;

    /**
     * le parametre initialise les options disponibles
     * 
     * @param o
     */
    public JRSTInterface(String o) {

        setFormats(o);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle("JRST");
        setLayout(new BorderLayout());
        add(getPanelPrincipal(), BorderLayout.CENTER);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setModal(true); // pour que JRST attende que cette fenetre soit
        // ferme
        pack();
        setResizable(false);
        setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2
                - getHeight() / 2); // Centrer
        setVisible(true);
    }

    private JPanel getPanelPrincipal() {
        if (panelPrincipal == null) {

            panelPrincipal = new JPanel();
            panelPrincipal.setLayout(new BoxLayout(panelPrincipal,
                    BoxLayout.Y_AXIS));
            panelPrincipal.add(getErrorLabel());
            panelPrincipal.add(getOuvrirPanel());
            panelPrincipal.add(getSavePanel());
            panelPrincipal.add(getFormatPanel());
            // panelPrincipal.add(getConvertPanel());
            panelPrincipal.add(getBoutonPanel());

        }
        return panelPrincipal;
    }

    private JLabel getErrorLabel() {
        if (errorLbl == null) {
            errorLbl = new JLabel("");
        }
        return errorLbl;
    }

    private JPanel getBoutonPanel() {

        if (boutonPanel == null) {
            boutonPanel = new JPanel();
            boutonPanel.setLayout(new FlowLayout());
            boutonPanel.add(getBoutonAnnuler());
            boutonPanel.add(getBoutonConvertir());

        }
        return boutonPanel;
    }

    private JButton getBoutonConvertir() {
        if (boutonConvertir == null) {
            boutonConvertir = new JButton();
            boutonConvertir.setText(_("btnConvert"));
            boutonConvertir.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    convert();
                }
            });
        }
        return boutonConvertir;
    }

    private JButton getBoutonAnnuler() {
        if (boutonAnnuler == null) {
            boutonAnnuler = new JButton();
            boutonAnnuler.setText(_("btnCancel"));
            boutonAnnuler.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    annuler();
                }
            });
        }
        return boutonAnnuler;
    }

    private JPanel getFormatPanel() {
        if (formatPanel == null) {
            composantsXSL = new LinkedList<Container>();
            ListAddXslPanel = new LinkedList<JPanel>();
            ListXslBouton = new LinkedList<JButton>();
            ListXslText = new LinkedList<JTextField>();
            ListXslBoutonLocation = new LinkedList<JButton>();
            formatPanel = new JPanel();
            formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.Y_AXIS));
            ButtonGroup group = new ButtonGroup();
            group.add(getFormat());
            group.add(getXslRadio());
            formatPanel.add(getFormatLigne());
            formatPanel.add(getXslPanel());
            for (JPanel p : getListAddXslPanel())
                formatPanel.add(p);
            // formatPanel.add(getAddXslPanel());
        }
        return formatPanel;
    }

    private LinkedList<JPanel> getListAddXslPanel() {
        if (ListAddXslPanel.size() == 0) {

            ListAddXslPanel.add(ajoutXSL());
            getFormatList().setEnabled(getFormat().isSelected());
            for (Container c : composantsXSL)
                c.setEnabled(!getFormat().isSelected());
        }
        return ListAddXslPanel;
    }

    private JPanel ajoutXSL() {
        JButton b = new JButton(more);
        b.setPreferredSize(new Dimension(20, 20));

        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton b = (JButton) e.getSource();
                if (b.getIcon() == more)

                    ajout();
                else
                    supprimerXslLigne((JButton) e.getSource());
            }
        });

        composantsXSL.add(b);
        ListXslBouton.add(b);
        JTextField t = new JTextField();
        t.setColumns(40);
        t.setVisible(false);
        composantsXSL.add(t);
        ListXslText.add(t);
        JButton b2 = new JButton(open);
        b2.setPreferredSize(new Dimension(30, 30));
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                openXslLocation((JButton) e.getSource());

            }

        });

        b2.setVisible(false);
        composantsXSL.add(b2);
        ListXslBoutonLocation.add(b2);
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(b);
        p.add(t);
        p.add(b2);
        return p;
    }

    private void supprimerXslLigne(JButton button) {
        boolean done = false;
        for (int i = 0; i < ListXslBouton.size() && !done; i++) {
            if (ListXslBouton.get(i) == button) {
                done = true;
                ListAddXslPanel.get(i).setVisible(false);
                ListXslBouton.remove(i);
                ListAddXslPanel.remove(i);
                ListXslBoutonLocation.remove(i);
                ListXslText.remove(i);
                pack();

            }
        }

    }

    private void ajout() {
        ListXslBoutonLocation.getLast().setVisible(true);
        ListXslText.getLast().setVisible(true);
        ListXslBouton.getLast().setIcon(delete);
        ListAddXslPanel.add(ajoutXSL());
        formatPanel.add(ListAddXslPanel.getLast());
        pack();

    }

    private JPanel getFormatLigne() {
        if (formatLigne == null) {
            formatLigne = new JPanel();
            formatLigne.setLayout(new FlowLayout(FlowLayout.LEADING));
            formatLigne.add(getFormat());
            formatLigne.add(getFormatList());

        }
        return formatLigne;
    }

    private JPanel getXslPanel() {
        if (xslLigne == null) {
            xslLigne = new JPanel();
            xslLigne.setLayout(new FlowLayout(FlowLayout.TRAILING));
            xslLigne.add(getXslRadio());
            xslLigne.add(getXslText());
            xslLigne.add(getBoutonXslLocation());

        }
        return xslLigne;
    }

    private JButton getBoutonXslLocation() {
        if (boutonXslLocation == null) {
            boutonXslLocation = new JButton(open);
            boutonXslLocation.setPreferredSize(new Dimension(30, 30));
            boutonXslLocation.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openXslLocation((JButton) e.getSource());
                }

            });
            boutonXslLocation.setEnabled(false);
            ListXslBoutonLocation.add(boutonXslLocation);
            composantsXSL.add(boutonXslLocation);
        }
        return boutonXslLocation;
    }

    private void openXslLocation(JButton b) {
        JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
        fc.showOpenDialog(this);
        File file = fc.getSelectedFile();
        if (file != null) {
            int i = 0;
            for (JButton btmp : ListXslBoutonLocation) {
                if (btmp == b) {
                    ListXslText.get(i).setText(file.getAbsolutePath());
                }

                i++;
            }
        }
    }

    private JTextField getXslText() {
        if (xslText == null) {
            xslText = new JTextField();
            xslText.setColumns(30);
            xslText.setEnabled(false);
            composantsXSL.add(xslText);
            ListXslText.add(xslText);
        }
        return xslText;
    }

    private JRadioButton getFormat() {
        if (format == null) {
            format = new JRadioButton("Format : ");
            format.setSelected(true);
            format.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    formatEnable();

                }

            });
        }
        return format;
    }

    private JRadioButton getXslRadio() {
        if (xslRadio == null) {
            xslRadio = new JRadioButton(_("externalXSL"));
        }
        return xslRadio;
    }

    private void formatEnable() {
        getFormatList().setEnabled(getFormat().isSelected());
        for (Container c : composantsXSL)
            c.setEnabled(!getFormat().isSelected());
    }

    private JComboBox getFormatList() {
        if (formatList == null) {
            formatList = new JComboBox(listFormats);
        }
        return formatList;
    }

    private JPanel getSavePanel() {
        if (savePanel == null) {
            savePanel = new JPanel();
            savePanel.setLayout(new FlowLayout());
            savePanel.add(new JLabel(_("saveAs")));
            savePanel.add(getSaveText());
            savePanel.add(getBoutonSaveLocation());
        }
        return savePanel;
    }

    private JButton getBoutonSaveLocation() {
        if (boutonSaveLocation == null) {
            boutonSaveLocation = new JButton(open);
            boutonSaveLocation.setPreferredSize(new Dimension(30, 30));
            boutonSaveLocation.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openSaveLocation();
                }

            });
        }
        return boutonSaveLocation;
    }

    private JPanel getOuvrirPanel() {
        if (openPanel == null) {
            openPanel = new JPanel();
            openPanel.setLayout(new FlowLayout());
            openPanel.add(new JLabel(_("open")));
            openPanel.add(getOpenText());
            openPanel.add(getBoutonOpenLocation());
        }
        return openPanel;
    }

    private JTextField getSaveText() {
        if (saveText == null) {
            saveText = new JTextField();
            saveText.setColumns(31);
        }
        return saveText;
    }

    private JTextField getOpenText() {
        if (openText == null) {
            openText = new JTextField();
            openText.setColumns(31);
        }
        return openText;
    }

    private JButton getBoutonOpenLocation() {
        if (boutonOpenLocation == null) {
            boutonOpenLocation = new JButton(open);
            boutonOpenLocation.setPreferredSize(new Dimension(30, 30));
            boutonOpenLocation.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openOpenLocation();
                }

            });
        }
        return boutonOpenLocation;
    }

    private void openOpenLocation() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
        fc.showOpenDialog(this);
        File file = fc.getSelectedFile();
        if (file != null) {
            getOpenText().setText(file.getAbsolutePath());
        }

    }

    private void openSaveLocation() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
        fc.showSaveDialog(this);
        File file = fc.getSelectedFile();
        if (file != null) {
            if (file.exists()) {
                int choix = askEcraser();
                if (choix == JOptionPane.YES_OPTION) {
                    ecrase = true;
                    getSaveText().setText(file.getAbsolutePath());
                } else if (choix == JOptionPane.NO_OPTION)
                    openSaveLocation();
            } else
                getSaveText().setText(file.getAbsolutePath());
        }

    }

    public int askEcraser() {
        int choix = JOptionPane.showConfirmDialog(this, _("overwriteGraph?"));

        return choix;
    }

    public void setFormats(String formats) {
        listFormats = formats.split("\\|");
    }

    private void annuler() {
        System.exit(0);
    }

    private void convert() {
        boolean exit = false;
        if (getOpenText().getText().equals("")) {
            getErrorLabel().setText(_("openEmpty?"));
            getErrorLabel().setForeground(Color.RED);
            pack();
        } else {
            if (!ecrase) {
                File file = new File(getSaveText().getText());
                if (file != null) {
                    if (file.exists()) {
                        int choix = askEcraser();
                        if (choix == JOptionPane.YES_OPTION)
                            ecrase = true;
                        else if (choix == JOptionPane.NO_OPTION)
                            exit = true;
                    }
                }
            }
            if (!exit) {
                String cmd = "";
                if (ecrase)
                    cmd += "--force ";
                if (getFormat().isSelected())
                    cmd += "-t " + getFormatList().getSelectedItem();
                else {
                    cmd += "-x ";
                    for (JTextField t : ListXslText) {
                        if (!t.getText().equals(""))
                            cmd += t.getText() + ",";
                    }
                    cmd = cmd.substring(0, cmd.length() - 1);
                }
                if (getSaveText().getText().length() > 0)
                    cmd += " -o " + getSaveText().getText();
                cmd += " " + getOpenText().getText() + " ";
                commande = cmd.trim().split(" ");
                dispose();
            }
        }
    }

    public String[] getCmd() {
        return commande;
    }

}
