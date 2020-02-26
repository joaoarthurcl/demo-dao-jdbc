package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class Main {

	public static void main(String[] args) {

		SellerDao sellerDao = DaoFactory.createSellerDao(); //instanciar seller
		
		System.out.println("======= TEST 1: seller findbyId =======");
		
		Seller seller = sellerDao.findById(3);
		
		System.out.println(seller);

	}

}
