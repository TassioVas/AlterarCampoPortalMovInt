package br.org.faepu.alterarCampo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.commons.net.ntp.TimeStamp;

import com.google.gson.JsonObject;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;

public class Program implements AcaoRotinaJava{
	
	BigDecimal nuNota;
	int codGrupo;
	BigDecimal codTipOper;
	
	int verificaTOP;
	
	String tipMov;
	String nomeUsu;
	
	Timestamp dhAlter;
	
	Timestamp agora = new Timestamp(System.currentTimeMillis());

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("inicio do codigo aqui");
		
		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		JapeSession.SessionHandle hnd = JapeSession.open();
		
		
		AlterarCampo alt = new AlterarCampo();
		Historico his = new Historico();
		
		String newCodTipOper = (String) contexto.getParam("CODTIPOPER");
		
		System.out.println(newCodTipOper);
		
		for (int i = 0; i < (contexto.getLinhas()).length ; i++ ) {
			Registro linha = contexto.getLinhas()[i];
			nuNota = (BigDecimal) linha.getCampo("NUNOTA");
		}
		
		BigDecimal usuarioLogado = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID();
		
		ResultSet codG = nativeSql.executeQuery("SELECT NOMEUSU, CODGRUPO FROM TSIUSU WHERE CODUSU = " + usuarioLogado);
		
		while (codG.next()) {
			nomeUsu = codG.getString("NOMEUSU");
			codGrupo = codG.getInt("CODGRUPO");
		}
		
		ResultSet rs = nativeSql.executeQuery("SELECT CODTIPOPER, TIPMOV FROM TGFCAB WHERE NUNOTA = " + nuNota);
		
		while (rs.next()) {
			codTipOper = rs.getBigDecimal("CODTIPOPER");
			tipMov = rs.getString("TIPMOV");
		}
		
		System.out.println("Tipmov " + tipMov);
		
		String letra = "J";
		
		if (tipMov.compareTo(letra) != 0 ) {
			contexto.setMensagemRetorno("Somente Pedidos Tipo \"J\" Sao permitidos nessa operação.\r\n No portal de movimentação Interna");
			return;
		}
		
		ResultSet top = nativeSql.executeQuery("SELECT DHALTER FROM TGFTOP WHERE CODTIPOPER = " + newCodTipOper);
		
		while (top.next()) {
			dhAlter = top.getTimestamp("DHALTER");
		}
		
		ResultSet codTop = nativeSql.executeQuery("SELECT CODTIPOPER FROM TGFTOP GROUP BY CODTIPOPER");
		
		while (codTop.next()) {
			verificaTOP = codTop.getInt("CODTIPOPER");
		}
		
	
		alt.alterar(newCodTipOper, nuNota, dhAlter);
		his.insereHistorico(nomeUsu, nuNota, newCodTipOper, codTipOper);
		
		
		contexto.setMensagemRetorno("TOP ALTERADA COM SUCESSO");
		
		JapeSession.close(hnd);
		
	}

}
