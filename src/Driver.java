import javax.swing.JOptionPane;

import bll.EditorBO;
import bll.FacadeBO;
import bll.IFacadeBO;
import dal.AbstractDAOEditorFactory;
import dal.FacadeDAO;
import dal.IDAOEditorFactory;
import dal.IEditorDBDAO;
import dal.IFacadeDAO;
import pl.EditorPO;

public class Driver {

	public Driver() {
    }

    public static void main(String[] args) {

    	IDAOEditorFactory factory = AbstractDAOEditorFactory.getInstance();
    	if (factory == null) {
    		JOptionPane.showMessageDialog(null,
    				"Failed to start the application: could not load configuration.\n"
    				+ "Please ensure config.properties exists and is correctly configured.",
    				"Startup Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}

    	IEditorDBDAO editorDAO = factory.createEditorDAO();
        IFacadeDAO facadeDAO = new FacadeDAO(editorDAO);
        IFacadeBO editorBO = new FacadeBO(new EditorBO(facadeDAO));
        new EditorPO(editorBO);
    }
}