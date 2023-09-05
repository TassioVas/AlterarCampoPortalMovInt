package br.org.faepu.alterarCampo;

import java.math.BigDecimal;

import com.hazelcast.hibernate.local.Timestamp;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.ws.ServiceContext;

public class AlterarCampo {

	String msg;
	public AuthenticationInfo authInfo;
	private ServiceContext sctx;

	public void alterar(String newCodTipOper, BigDecimal nuNota, java.sql.Timestamp dhAlter ) throws Exception {
		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		JapeSession.SessionHandle hnd = JapeSession.open();

		BigDecimal codTipOper = new BigDecimal(newCodTipOper);

		try {

			boolean disable = nativeSql.executeUpdate(" ALTER TABLE TGFCAB DISABLE TRIGGER TRG_FAE_UPD_TGFCAB");
			
			JapeWrapper topDAO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA);
			topDAO.prepareToUpdateByPK(nuNota).set("CODTIPOPER", codTipOper).set("DHTIPOPER", dhAlter).update();
			
			boolean update = nativeSql.executeUpdate("UPDATE TSILIB SET CODTIPOPER = "+ codTipOper+ "WHERE NUCHAVE = "+nuNota);

			boolean enable = nativeSql.executeUpdate(" ALTER TABLE TGFCAB ENABLE TRIGGER TRG_FAE_UPD_TGFCAB");

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na inclusao do item " + e.getMessage();
			System.out.println(msg);
		}

		JapeSession.close(hnd);
	}

}

