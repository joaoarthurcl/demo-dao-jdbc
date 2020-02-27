package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao { // o dao vai ter uma dependencia com a conexao e vamos criar um
													// atributo de conexao

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		
		try {
			
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			System.out.println("Done! Rows affected " + rowsAffected);
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
PreparedStatement st = null;
		
		try {
			
			st = conn.prepareStatement(
					"UPDATE seller " 
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Seller obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// vamos buscar todos os campos do seller + nome do departamento o nome do
			// departamento
			// ganhou o apelido de DepName, faz um join para buscar os dados de vendedor e
			// departamento
			// onde o ID do vendedor seja igual a ?
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery(); // o resultado do comando sql cai na variavel rs
			if (rs.next()) { // para testar se veio algum resultado. se retornou, temos que navegar pelos
								// dados e instanciar o vendedor
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs, dep);
				return obj; // criamos o obj seller e retornar um seller por ID
			}
			return null; // se a consulta nºao encontrou vendedor
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// vamos buscar todos os campos do seller + nome do departamento o nome do
			// departamento
			// ganhou o apelido de DepName, faz um join para buscar os dados de vendedor e
			// departamento
			// e ordenar por nome
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "ORDER BY Name");

			rs = st.executeQuery(); // o resultado do comando sql cai na variavel rs

			// como o resultado pode ter 0 ou mais valores, não pode ser um if, tem que ser
			// um while,
			// para percorrer o rs enquanto tiver um proximo.

			List<Seller> list = new ArrayList<>();// como são varios valores, vamos declarar uma lista
			Map<Integer, Department> map = new HashMap<>(); // será nosso controle para não repetir o mesmo
															// departamento.
			// o map vazio foi criado para guardar qualquer departamento que for
			// instanciado.
			while (rs.next()) {
				// a cada vez que passar no while, vamos testar se o departamento já existe
				Department dep = map.get(rs.getInt("DepartmentId"));// vamos passar o id do departamento que estiver no
																	// rs.
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);// agora vou salvar o departamento, para que da proxima vez,
															// ele passar no map ver que já existe
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj); // adicionamos o seller na lista
			}
			return list; // retorna a lista
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// vamos buscar todos os campos do seller + nome do departamento o nome do
			// departamento
			// ganhou o apelido de DepName, faz um join para buscar os dados de vendedor e
			// departamento ordenando por nome
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");

			st.setInt(1, department.getId());

			rs = st.executeQuery(); // o resultado do comando sql cai na variavel rs

			// como o resultado pode ter 0 ou mais valores, não pode ser um if, tem que ser
			// um while,
			// para percorrer o rs enquanto tiver um proximo.

			List<Seller> list = new ArrayList<>();// como são varios valores, vamos declarar uma lista
			Map<Integer, Department> map = new HashMap<>(); // será nosso controle para não repetir o mesmo
															// departamento.
			// o map vazio foi criado para guardar qualquer departamento que for
			// instanciado.
			while (rs.next()) {
				// a cada vez que passar no while, vamos testar se o departamento já existe
				Department dep = map.get(rs.getInt("DepartmentId"));// vamos passar o id do departamento que estiver no
																	// rs.
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);// agora vou salvar o departamento, para que da proxima vez,
															// ele passar no map ver que já existe
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj); // adicionamos o seller na lista
			}
			return list; // retorna a lista
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
