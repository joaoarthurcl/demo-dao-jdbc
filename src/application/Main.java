package application;

import java.util.Date;

import model.entities.Department;
import model.entities.Seller;

public class Main {

	public static void main(String[] args) {
		
		Department obj = new Department(1, "books");
		
		Seller seller = new Seller(1, "João", "joao@gmail.com", new Date(), 4000.00, obj);
		System.out.println(seller);
	}

}
