import bll.EditorBO;
import bll.FacadeBO;
import bll.IFacadeBO;
import bll.LoginBO;
import bll.ILoginBO;
import dal.AbstractDAOEditorFactory;
import dal.FacadeDAO;
import dal.IEditorDBDAO;
import dal.IFacadeDAO;
import dal.UserDAO;
import pl.EditorPO;
import pl.LoginPO;

public class Driver {

	public Driver() {
    }

    public static void main(String[] args) {

    	IEditorDBDAO editorDAO = AbstractDAOEditorFactory.getInstance().createEditorDAO();
        IFacadeDAO facadeDAO = new FacadeDAO(editorDAO);
        IFacadeBO editorBO = new FacadeBO(new EditorBO(facadeDAO));
        ILoginBO loginBO = new LoginBO(new UserDAO());

        new LoginPO(loginBO, unused -> new EditorPO(editorBO));
    }
}