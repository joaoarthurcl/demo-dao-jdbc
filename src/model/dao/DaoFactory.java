package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory { //classe aux responsavel por instanciar meus daos
	
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC();
	}

}
