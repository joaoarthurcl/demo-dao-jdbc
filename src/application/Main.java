package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Main {

	public static void main(String[] args) {

		SellerDao sellerDao = DaoFactory.createSellerDao(); //instanciar seller
		
		System.out.println("======= TEST 1: seller findbyId =======");
		
		Seller seller = sellerDao.findById(3);
		System.out.println(seller);

		
		System.out.println("======= TEST : seller findbyDepartment =======");
		Department department = new Department(2, null);
		List<Seller> list = sellerDao.findByDepartment(department);
		for(Seller obj : list) {
			System.out.println(obj);
		}
		
		
		
	}

}
