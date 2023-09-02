package ru.mpei.brics.appRepositoryLayer;

import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//import ru.mpei.brics.extention.dto.Measurement;

//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;


@Repository
//@Transactional(readOnly = true)
public class RepositoryImpl implements RepositoryInterface {

//    @PersistenceContext
//    private EntityManager em;

//    @Override
//    @Transactional
//    public void save(Measurement m) {
//        if(m.getId() == 0){
//            em.persist(m);
//        } else {
//            em.merge(m);
//        }
//    }
//
//    @Override
//    public List<Measurement> getMeasurements(int startIndex, int endIndex) {
//        long start = startIndex;
//        long end = endIndex;
//        List<Measurement> mList = em.createQuery("select m from Measurement m where m.id >= :startIndex and m.id <= :endIndex")
//                        .setParameter("startIndex", start).setParameter("endIndex", end)
//                        .getResultList();
//        return mList;
//    }
}
