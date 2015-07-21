package emoDAO;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import emoCoreServiceObjects.ResourceModel;
import emoCoreServiceObjects.Simulation;

public class ResourceModelDAOImpl implements ResourceModelDAO {

	private SessionFactory sessionFactory;
	 
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
	
	@Override
	public void saveModel(ResourceModel resModel) {
		Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(resModel);
        tx.commit();
        session.close();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceModel> getModelsBySimUserId(String userId, Long simId) {
		Session session = this.sessionFactory.openSession();
		Criteria cr = session.createCriteria(ResourceModel.class);
		cr.add(Restrictions.eq("userId", userId));
		cr.add(Restrictions.eq("simId", simId));
		List<ResourceModel> modelsList = cr.list();
		
		return modelsList;
	}

}
