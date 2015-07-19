package emoDAO;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import emoCoreServiceObjects.Simulation;

public class SimulationDAOImpl implements SimulationDAO {
	
	private SessionFactory sessionFactory;
	 
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	@Override
	public void saveSimulation(Simulation sim) {
		Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(sim);
        tx.commit();
        session.close();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Simulation> getSimulationsByUser(String userId) {
		Session session = this.sessionFactory.openSession();
		Criteria cr = session.createCriteria(Simulation.class);
		cr.add(Restrictions.eq("user_id", userId));
		List<Simulation> simList = cr.list();
		
		return simList;
	}

}
