package emoDAO;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import emoCoreServiceObjects.ObservedEmotions;
import emoCoreServiceObjects.ResourceModel;

public class ObservedEmotionsDAOImpl implements ObservedEmotionsDAO {

	private SessionFactory sessionFactory;
	 
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
	
    @SuppressWarnings("unchecked")
	@Override
	public List<ObservedEmotions> getObsEmosByUserSimId(String userId,
			Long simId) {
		Session session = this.sessionFactory.openSession();
		Criteria cr = session.createCriteria(ObservedEmotions.class);
		cr.add(Restrictions.eq("userId", userId));
		cr.add(Restrictions.eq("simId", simId));
		List<ObservedEmotions> obsEmosList = cr.list();
		
		return obsEmosList;
	}

}
