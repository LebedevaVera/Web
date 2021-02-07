package de.hsb.app.kv.controller;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import de.hsb.app.kv.model.Anrede;
import de.hsb.app.kv.model.Kunde;
import java.io.Serializable;

@Named("kundenHandler")
@SessionScoped
@FacesConfig
public class KundenHandler implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/*Für das Handling der Entity „Kunde“ müssen wir einen EntityManager deklarieren. 
	 * Darüber hinaus benötigen wir einen externen Transaktionsmanager:*/
	@PersistenceContext(name = "kv-persistence-unit")
	private EntityManager em;
	@Resource
	private UserTransaction utx;
	private DataModel<Kunde> kunden;
	private Kunde merkeKunde = new Kunde();
	
	@PostConstruct
	public void init() {
		System.out.println("init()");
		try {
			utx.begin();
			em.persist(new Kunde(Anrede.HERR, "Hugo", "Hermann", new GregorianCalendar(1999, 3, 15).getTime())); 
			//...
			kunden = new ListDataModel<>();
			kunden.setWrappedData(em.createNamedQuery("SelectKunden").getResultList());
			utx.commit();
		} catch (NotSupportedException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (RollbackException e) {
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			e.printStackTrace();
		}
	}
	
	public String neu(Kunde k) {
		System.out.println("neu() : " + k.getVorname());
		return "neuerKunde";
	}
	
	@Transactional
	public String speichern() {
		merkeKunde = em.merge(merkeKunde);
		em.persist(merkeKunde);
		kunden.setWrappedData(em.createNamedQuery("SelectKunden").getResultList());
		return "alleKunden";
	}
	
	@Transactional
    public String delete() {
        System.out.println("delete()");
        merkeKunde = kunden.getRowData();
        merkeKunde = em.merge(merkeKunde);
        em.remove(merkeKunde);
        kunden.setWrappedData(em.createNamedQuery("SelectKunden").getResultList());
        return "alleKunden";
    }
	
	@Transactional
	public String edit() {
		System.out.println("edit()");
		merkeKunde = kunden.getRowData();
		return "neuerKunde";
	}

	public DataModel<Kunde> getKunden() {
		return kunden;
	}

	public void setKunden(DataModel<Kunde> kunden) {
		this.kunden = kunden;
	}

	public Kunde getMerkeKunde() {
		return merkeKunde;
	}

	public void setMerkeKunde(Kunde merkeKunde) {
		this.merkeKunde = merkeKunde;
	}
	
	public Anrede[] getAnredeValues() {
        return Anrede.values();
    }
	
}