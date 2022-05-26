package dao;

import java.util.concurrent.Future;

public interface UserDAO {
	
	public Future<String> infoModel() throws InterruptedException;

	public Future<String> infoUser() throws InterruptedException;

}
