package org.codelutin.jrst;

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
import java.util.Locale;
import java.util.ResourceBundle;

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

import org.codelutin.i18n.I18n;

public class JRSTInterface extends JDialog{
    private JPanel savePanel = null;

    private JPanel formatPanel = null;

    private JPanel openPanel = null;

    private JPanel formatLigne = null;

    private JPanel xslLigne = null;

    private JComboBox formatList = null;

    private JButton boutonAnnuler = null;
    
    private JButton boutonConvertir = null;

    private JPanel panelPrincipal = null;

    private JPanel boutonPanel = null;

    private JButton boutonSaveLocation = null;
    
    private JLabel errorLbl=null;

    private JButton boutonOpenLocation = null;

    private JButton boutonXslLocation = null;

    private String[] listFormats = null;

    private JTextField saveText = null;

    private JTextField openText = null;

    private JTextField xslText = null;

    private JRadioButton format = null;

    private JRadioButton xslRadio = null;

    private LinkedList<JPanel> ListAddXslPanel = null;

    private LinkedList<JTextField> ListXslText = null;

    private LinkedList<JButton> ListXslBouton = null;

    private LinkedList<JButton> ListXslBoutonLocation = null;
    private boolean ecrase=false;
    private String[] commande = null;

    private ImageIcon open = new ImageIcon("src/resources/icone/open.png");
    private ImageIcon delete = new ImageIcon("src/resources/icone/cancel.png");
    private ImageIcon more = new ImageIcon("src/resources/icone/more.gif");
    private ResourceBundle bundle = null;

    private LinkedList<Container> composantsXSL = null;
    /**
     * le parametre initialise les options disponibles
     * @param String 
     *              o
     */
    public JRSTInterface(String o) {
        // language
        if (Locale.getDefault().getLanguage() == "fr")
            I18n.init("fr", "FR");
        else
            I18n.init("en", "US");
        bundle = ResourceBundle.getBundle("org.codelutin.i18n.I18nBundleBridge");
       this.setFormats(o);
       this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
       this.setTitle("JRST");
       this.setLayout(new BorderLayout());
       this.add(getPanelPrincipal(), BorderLayout.CENTER);
       Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
       this.setModal(true);  // pour que JRST attende que cette fenetre soit ferme
       this.pack(); 
       this.setResizable(false);  
       this.setLocation(dim.width/2 - this.getWidth()/2, dim.height/2 - this.getHeight()/2); // Centrer
       this.setVisible(true);
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
    private JLabel getErrorLabel(){
        if (errorLbl==null){
            errorLbl=new JLabel("");
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
            boutonConvertir.setText(bundle.getString("btnConvert"));
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
            boutonAnnuler.setText(bundle.getString("btnCancel"));
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
        if (ListAddXslPanel.size()==0) {

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
                if (b.getIcon()==more)
                    
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
                this.pack();

            }
        }

    }

    private void ajout() {
        ListXslBoutonLocation.getLast().setVisible(true);
        ListXslText.getLast().setVisible(true);
        ListXslBouton.getLast().setIcon(delete);
        ListAddXslPanel.add(ajoutXSL());
        formatPanel.add(ListAddXslPanel.getLast());
        this.pack();

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
            xslRadio = new JRadioButton(bundle.getString("externalXSL"));
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
            savePanel.add(new JLabel(bundle.getString("saveAs")));
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
            openPanel.add(new JLabel(bundle.getString("open")));
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
            if (file.exists()){
                int choix = askEcraser();
                if (choix == JOptionPane.YES_OPTION){
                    ecrase = true;
                    getSaveText().setText(file.getAbsolutePath());
                }
                else if (choix == JOptionPane.NO_OPTION)
                    openSaveLocation();
            }
            else
                getSaveText().setText(file.getAbsolutePath());
        }

    }
    public int askEcraser(){
        int choix = JOptionPane.showConfirmDialog(this,bundle.getObject("overwriteGraph?"));
             
        return choix;
    }
    public void setFormats(String formats) {
        listFormats = formats.split("\\|");
    }
   

    private void annuler() {
        System.exit(0);
    }
    
    
    private void convert() {
        boolean exit=false;
        if (getOpenText().getText().equals("")){
            getErrorLabel().setText(bundle.getString("openEmpty?"));
            getErrorLabel().setForeground(Color.RED);
            this.pack();
        }
        else{
            if (!ecrase){
                File file = new File(getSaveText().getText());
                if (file != null) {
                    if (file.exists()){
                        int choix = askEcraser();
                        if (choix == JOptionPane.YES_OPTION)
                            ecrase = true;
                        else if (choix == JOptionPane.NO_OPTION)
                            exit=true;
                    }                    
                }
            }
            if (!exit){
                String cmd = "";
                if (ecrase)
                    cmd += "--force ";
                if (getFormat().isSelected())
                    cmd += "-t " + getFormatList().getSelectedItem();
                else{
                    cmd += "-x ";
                    for (JTextField t : ListXslText){
                        if (!t.getText().equals(""))
                            cmd+=t.getText()+",";
                    }
                    cmd=cmd.substring(0,cmd.length()-1);
                }
                if (getSaveText().getText().length() > 0)
                    cmd += " -o " + getSaveText().getText();
                cmd += " " + getOpenText().getText() + " ";
                commande = cmd.trim().split(" ");
                this.dispose();
            }
        }
    }
    public String[] getCmd(){
        return commande;
    }

}
