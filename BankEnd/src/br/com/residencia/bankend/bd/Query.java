package br.com.residencia.bankend.bd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import br.com.residencia.bankend.clientes.Cliente;
import br.com.residencia.bankend.contas.Comprovante;
import br.com.residencia.bankend.contas.ContaCorrente;
import br.com.residencia.bankend.contas.ContaPoupanca;
import br.com.residencia.bankend.contas.Contas;
import br.com.residencia.bankend.contas.SeguroVida;
import br.com.residencia.bankend.contas.Tributos;
import br.com.residencia.bankend.funcionarios.Diretor;
import br.com.residencia.bankend.funcionarios.Funcionario;
import br.com.residencia.bankend.funcionarios.Gerente;
import br.com.residencia.bankend.funcionarios.Presidente;
import br.com.residencia.bankend.utility.ClienteTableModel;
import br.com.residencia.bankend.utility.FuncionarioTableModel;

public class Query {
	private Connection conexao = null;
	private PreparedStatement st = null;
	private ResultSet rs = null;

	public Query(Connection conexao) {

		this.conexao = conexao;
	}

	public Funcionario funcionario(String emaill, String senhaa) {
		Funcionario fun = null;
		try {
			st = conexao.prepareStatement("select * from funcionario where email = ? and senha =? ");
			st.setString(1, emaill);
			st.setString(2, senhaa);
			st.execute();

			rs = st.getResultSet();

			if (rs.next()) {

				String nome = rs.getString("nome");
				String sobreNome = rs.getString("sobrenome");
				String cargo = rs.getString("cargo");
				String cpf = rs.getString("cpf");
				String email = rs.getString("email");
				String senha = rs.getString("senha");
				int acesso = rs.getInt("acesso");
				double salario = rs.getDouble("salario");

				switch (acesso) {
				case 1:
					fun = new Gerente(nome, sobreNome, cargo, cpf, email, senha, salario, acesso, null);

					break;
				case 2:
					fun = new Diretor(nome, sobreNome, cargo, cpf, email, senha, salario, acesso, null);

					break;
				case 3:
					fun = new Presidente(nome, sobreNome, cargo, cpf, email, senha, salario, acesso);
					break;

				}

			} else {
				System.out.println("nao existe  funcionario");
			}

		} catch (Exception e) {

		}
		return fun;
	}

	public Cliente cliente(String emaill, String senhaa) {

		Cliente cliente = null;
		{

			try {

				st = conexao.prepareStatement("select * from cliente where email = ? and senha =? ");
				st.setString(1, emaill);
				st.setString(2, senhaa);
				st.execute();

				st.execute();

				rs = st.getResultSet();

				if (rs.next()) {
					int id = rs.getInt("IdCliente");
					String nome = rs.getString("nome");
					String sobrenome = rs.getString("sobrenome");
					String email = rs.getString("email");
					String cpf = rs.getString("cpf");
					String senha = rs.getString("senha");

					cliente = new Cliente(nome, sobrenome, email, cpf, senha, id);

					return cliente;
				}

				else {
					System.out.println("cliente n encontrado");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			return cliente;
		}

	}

	public Funcionario dadosFuncionario(String login, String senha) {
		Funcionario fun = null;

		try {
			st = conexao.prepareStatement("Select *from funcionario where email =? and senha=?");
			st.setString(1, login);
			st.setString(2, senha);
			st.execute();

			rs = st.getResultSet();

			if (rs.next()) {
				int id = rs.getInt("IDFuncionario");
				String nome = rs.getString("nome");
				String sobrenome = rs.getString("sobrenome");
				String cargo = rs.getString("cargo");
				double salario = rs.getDouble("salario");
				String cpf = rs.getString("cpf");
				String senha2 = rs.getString("senha");
				String email = rs.getString("email");
				int acesso = rs.getInt("acesso");
				int numConta = rs.getInt("numConta");
				String idagencia = rs.getString("agencia");

				switch (acesso) {
				case 1:
					Gerente gerente = new Gerente(nome, sobrenome, cargo, cpf, email, senha2, 2000.00, 1, idagencia);
					fun = gerente;

					break;

				case 2:
					Diretor diretor = new Diretor(nome, sobrenome, cargo, cpf, email, senha2, 3000.00, 2, null);
					fun = diretor;
					break;

				case 3:
					Presidente presidente = new Presidente(nome, sobrenome, cargo, cpf, email, senha2, 4000.00, 3);
					fun = presidente;
					break;

				default:
					break;
				}

				return fun;

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public Contas descobreConta(Cliente cliente) {

		Contas continha = null;

		try {
			st = conexao.prepareStatement("select * from contas,cliente where id_cliente=?");

			st.setInt(1, cliente.getId());
			st.execute();

			rs = st.getResultSet();

			if (rs.next()) {
				String numero = rs.getString("numero");
				double saldo = rs.getFloat("saldo");
				String tipo = rs.getString("tipo");
				String agencia = rs.getString("agencia");
				int IdConta = rs.getInt("IdConta");

				SeguroVida seguro = verificaSeguro(IdConta);

				if (tipo.equals("corrente")) {
					ContaCorrente corrente = new ContaCorrente(agencia, numero, tipo, saldo, cliente, seguro, IdConta);
					continha = corrente;
				} else {
					ContaPoupanca poupanca = new ContaPoupanca(agencia, numero, tipo, saldo, cliente, seguro, IdConta);
					continha = poupanca;
				}

			}

			return continha;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return continha;
	}

	public Contas verificaConta(String conta) {

		Contas continha = null;

		try {

			st = conexao.prepareStatement("select *from contas where numero = ?  ");

			st.setString(1, conta);

			st.execute();

			rs = st.getResultSet();

			if (rs.next()) {
				int idConta = rs.getInt("IDConta");
				String numero = rs.getString("numero");
				double saldo = rs.getDouble("saldo");
				String tipo = rs.getString("tipo");
				String agencia = rs.getString("agencia");

				SeguroVida seguro = verificaSeguro(idConta);

				if (tipo.equals("corrente")) {
					Cliente cliente = descobreCliente(idConta);
					ContaCorrente corrente = new ContaCorrente(agencia, numero, tipo, saldo, cliente, seguro, idConta);

					continha = corrente;
				}

				if (tipo.equals("poupanca")) {
					Cliente cliente = descobreCliente(idConta);

					ContaPoupanca poupanca = new ContaPoupanca(agencia, numero, tipo, saldo, cliente, seguro, idConta);
					continha = poupanca;

				}

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return continha;

	}

	public boolean contaExiste(String conta) {

		Contas continha = null;

		try {

			st = conexao.prepareStatement("select *from contas where numero = ?  ");

			st.setString(1, conta);

			st.execute();

			rs = st.getResultSet();

			if (rs.next()) {
				return true;

			}

			else {
				return false;
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return false;
	}

	public Cliente descobreCliente(int id) {

		Cliente clientee = null;

		try {
			st = conexao.prepareStatement("Select *from cliente where idCliente = ?");

			st.setInt(1, id);

			st.execute();

			rs = st.getResultSet();

			if (rs.next()) {
				int idCliente = rs.getInt("IdCliente");
				String nome = rs.getString("nome");
				String sobrenome = rs.getString("sobrenome");
				String email = rs.getString("email");
				String cpf = rs.getString("cpf");
				String senha = rs.getString("senha");

				Cliente cliente = new Cliente(nome, sobrenome, email, cpf, senha, idCliente);
				clientee = cliente;
			}

		} catch (SQLException e) {
			System.out.println("DEU MERDA");
			e.printStackTrace();
		}
		return clientee;

	}

	public void atualizarTransferencia(Contas remetente, Contas destinatario) {
		try {

			// retirando o valor
			st = conexao.prepareStatement("Update Contas set saldo = ? where numero=?  ");
			st.setDouble(1, remetente.getSaldo());
			st.setString(2, remetente.getNumero());

			st.executeUpdate();

			// add o valor
			st = conexao.prepareStatement("Update Contas set saldo = ? where numero=?  ");
			st.setDouble(1, destinatario.getSaldo());
			st.setString(2, destinatario.getNumero());

			st.executeUpdate();

			// Add tributo

			st = conexao.prepareStatement("select quantidadeTransf from Contas where IdConta=? ");
			st.setInt(1, remetente.getId());
			st.execute();

			rs = st.getResultSet();

			int qtdTransf = 0;

			if (rs.next()) {
				qtdTransf = rs.getInt("quantidadeTransf");
			}

			if (remetente.getTipo().equals("corrente")) {
				st = conexao.prepareStatement("Update Contas set quantidadeTransf = ? where IDCONTA=? ");
				st.setInt(1, qtdTransf + 1);
				st.setInt(2, remetente.getId());
				st.executeUpdate();

				totalTributo(remetente);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void adicionarTributo(Contas contas) {
		ContaCorrente corrente = null;
		ContaPoupanca poupanca = null;

	}

	public void deposito(Contas contaDestinatario, double valor, Contas remetente) {

		try {

			st = conexao.prepareStatement("UPDATE CONTAS SET SALDO = ? WHERE NUMERO=?");
			st.setDouble(1, contaDestinatario.getSaldo());
			st.setString(2, contaDestinatario.getNumero());
			st.executeUpdate();

			// Add tributo

			st = conexao.prepareStatement("select quantidadeDeposito from Contas where IdConta=? ");
			st.setInt(1, contaDestinatario.getId());
			st.execute();

			rs = st.getResultSet();

			int qtdTransf = 0;

			if (rs.next()) {
				qtdTransf = rs.getInt("quantidadeDeposito");
			}

			if (remetente.getTipo().equals("corrente")) {
				st = conexao.prepareStatement("Update Contas set quantidadeDeposito = ? where IDCONTA=? ");
				st.setInt(1, qtdTransf + 1);
				st.setInt(2, remetente.getId());
				st.executeUpdate();

				totalTributo(remetente);

			}

			// diminuindo a taxa
			st = conexao.prepareStatement("UPDATE CONTAS SET SALDO = ? WHERE NUMERO=?");
			st.setDouble(1, remetente.getSaldo());
			st.setString(2, remetente.getNumero());
			st.executeUpdate();

		} catch (SQLException e) {
			System.out.println("informe um valor aceito");
			e.printStackTrace();
		}

	}

	public void saque(Contas contaDestinatario, double valor) {

		try {

			st = conexao.prepareStatement("UPDATE CONTAS SET SALDO = ? WHERE NUMERO=?");
			st.setDouble(1, contaDestinatario.getSaldo());
			st.setString(2, contaDestinatario.getNumero());
			st.executeUpdate();

			// Add tributo

			st = conexao.prepareStatement("select quantidadeSaque from Contas where IdConta=? ");
			st.setInt(1, contaDestinatario.getId());
			st.execute();

			rs = st.getResultSet();

			int qtdTransf = 0;

			// pego a quantidade de saque e somo+1
			if (rs.next()) {
				qtdTransf = rs.getInt("quantidadeSaque");
			}

			if (contaDestinatario.getTipo().equals("corrente")) {

				st = conexao.prepareStatement("Update Contas set quantidadeSaque = ? where IDCONTA=? ");
				st.setInt(1, qtdTransf + 1);
				st.setInt(2, contaDestinatario.getId());
				st.executeUpdate();

				totalTributo(contaDestinatario);
			}

		}

		catch (Exception e) {
			// TODO: handle exception
		}

	}

	public int qtdAgencia(String agencia) {
		int qtdConta = 0;

		try {
			st = conexao.prepareStatement("select *from contas where agencia =?");

			st.setString(1, agencia);
			st.execute();

			rs = st.getResultSet();

			while (rs.next()) {
				qtdConta += 1;

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return qtdConta;

	}

	public double valorTotal() {
		try {
			st = conexao.prepareStatement("select saldo from contas");
			st.execute();

			rs = st.getResultSet();

			double saldoTotal = 0;

			while (rs.next()) {
				saldoTotal += rs.getDouble("saldo");
			}

			return saldoTotal;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;

	}

	// metodo que verifica se o funcionario possui uma conta cadastrada

	public boolean contaFuncionario(String login, String senha) {
		try {
			st = conexao.prepareStatement("select *from funcionario where email=? and senha =?");
			st.setString(1, login);
			st.setString(2, senha);
			st.execute();
			rs = st.getResultSet();

			if (rs.next()) {
				st = conexao.prepareStatement("select *from cliente where email=? and senha =?");
				st.setString(1, login);
				st.setString(2, senha);
				st.execute();
				rs = st.getResultSet();

				if (rs.next()) {
					return true;
				} else {
					return false;
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
//att
	}

	public void addDadosRelatorios(FuncionarioTableModel tabelaFuncionario, ArrayList<Contas> listaContas) {

		for (Contas conta : listaContas) {

			tabelaFuncionario.adicionarLinha(conta);
		}
	}

	// ordena por nome
	public void addAllClientes(ClienteTableModel tabelaContas) {

		ArrayList<Contas> listaContas = new ArrayList<Contas>();

		try {

			// pegando todas as contas e ordenando
			st = conexao.prepareStatement("SELECT *FROM CONTAS,cliente where idcliente=id_cliente ORDER BY nome ASC");
			st.execute();

			rs = st.getResultSet();

			while (rs.next()) {

				// cliente
				int idCliente = rs.getInt("IDCliente");
				String nome = rs.getString("nome");
				String sobrenome = rs.getString("sobrenome");
				String cpf = rs.getString("cpf");

				Cliente cliente = new Cliente(nome, sobrenome, null, cpf, null, idCliente);

				// dados conta
				int id = rs.getInt("IDConta");
				String agencia = rs.getString("agencia");
				String tipo = rs.getString("tipo");
				String numero = rs.getString("numero");
				// verificando o tipo

				if (tipo.equals("poupanca")) {

					ContaPoupanca poupanca = new ContaPoupanca(agencia, numero, tipo, null, cliente, null, id);

					listaContas.add(poupanca);
					tabelaContas.adicionarLinha(poupanca);

				}

				if (tipo.equals("corrente")) {

					ContaCorrente corrente = new ContaCorrente(agencia, numero, tipo, null, cliente, null, id);

					listaContas.add(corrente);
					tabelaContas.adicionarLinha(corrente);

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void totalTributo(Contas conta) {

		ArrayList<Contas> contas = new ArrayList<>();

		try {
			st = conexao.prepareStatement("Select *from contas where idConta = ?");
			st.setInt(1, conta.getId());
			st.execute();

			rs = st.getResultSet();

			while (rs.next()) {
				int quantidadeTransf = rs.getInt("quantidadeTransf");
				int quantidadeSaque = rs.getInt("quantidadeSaque");
				int quantidadeDeposito = rs.getInt("quantidadeDeposito");

				Tributos transferencia = new Tributos("transferencia", quantidadeTransf);
				Tributos saque = new Tributos("saque", quantidadeSaque);
				Tributos deposito = new Tributos("deposito", quantidadeDeposito);

				ArrayList<Tributos> trib = new ArrayList<>();
				trib.add(saque);
				trib.add(deposito);
				trib.add(transferencia);

				ContaCorrente corrente = (ContaCorrente) conta;

				corrente.setTributos(trib);

				Comprovante.tributos(corrente);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public SeguroVida verificaSeguro(int idConta) {
		try {
			st = conexao.prepareStatement("Select *from seguroVida where id_Conta = ?");
			st.setInt(1, idConta);
			st.execute();

			rs = st.getResultSet();
			if (rs.next()) {

				int idSeguro = rs.getInt("idSeguro");
				double taxa = rs.getDouble("taxa");
				double valor = rs.getDouble("valor");

				SeguroVida seguro = new SeguroVida(valor);
				seguro.setTaxa(taxa);
				seguro.setValor(valor);

				return seguro;

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public void instanciaSeguro(Contas conta) {
		try {

			st = conexao.prepareStatement("INSERT INTO seguroVida (taxa,valor,ativo,id_conta) VALUES(?,?,?,?)");
			st.setDouble(1, conta.getSeguro().getTaxa());
			st.setDouble(2, conta.getSeguro().getValor());
			st.setInt(3, 1);
			st.setInt(4, conta.getId());
			st.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ordena por nomeeee2e
	public void trazerRelatorio(ArrayList<Contas> listaContas, Funcionario fun) {

		try {

			String agenciaFuncionario = null;

			st = conexao.prepareStatement("select *from funcionario where cpf=?");
			st.setString(1, fun.getCpf());
			st.execute();

			rs = st.getResultSet();
			if (rs.next()) {
				agenciaFuncionario = rs.getString("agencia");
			}

			// pegando todas as contas e ordenando
			st = conexao.prepareStatement(
					"SELECT *FROM CONTAS,cliente where idcliente=id_cliente and  agencia =? ORDER BY nome ASC");
			st.setString(1, agenciaFuncionario);
			st.execute();

			rs = st.getResultSet();

			while (rs.next()) {

				// cliente
				int idCliente = rs.getInt("IDCliente");
				String nome = rs.getString("nome");
				String sobrenome = rs.getString("sobrenome");
				String cpf = rs.getString("cpf");

				Cliente cliente = new Cliente(nome, sobrenome, null, cpf, null, idCliente);

				// dados conta
				int id = rs.getInt("IDConta");
				String agencia = rs.getString("agencia");
				String tipo = rs.getString("tipo");
				String numero = rs.getString("numero");
				// verificando o tipo

				if (tipo.equals("poupanca")) {

					ContaPoupanca poupanca = new ContaPoupanca(agencia, numero, tipo, null, cliente, null, id);

					listaContas.add(poupanca);

				}

				if (tipo.equals("corrente")) {

					ContaCorrente corrente = new ContaCorrente(agencia, numero, tipo, null, cliente, null, id);

					listaContas.add(corrente);

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
