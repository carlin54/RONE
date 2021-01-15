package rone.backend.garudahandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import jp.sbi.garuda.backend.GarudaBackend;
import jp.sbi.garuda.backend.POJOs.CompatibleGadgetDetails;
import jp.sbi.garuda.backend.exception.NoFileToSendException;
import jp.sbi.garuda.backend.incomingHandler.IncomingRequestProtocolHandler;
import jp.sbi.garuda.backend.incomingHandler.garudaActionListeners.requests.LoadDataRequestActionListener;
import jp.sbi.garuda.backend.incomingHandler.garudaActionListeners.responses.ActivateGadgetResponseActionListener;
import jp.sbi.garuda.backend.incomingHandler.garudaActionListeners.responses.GetCompatibleGadgetListResponseActionListener;
import jp.sbi.garuda.backend.incomingHandler.responseCodes.GarudaResponseCode;
import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import jp.sbi.garuda.backend.plugin.pipeline.GarudaBackendPipelinePlugin;
import jp.sbi.garuda.backend.plugin.pipeline.exceptions.PipelineNotInitializedException;
import jp.sbi.garuda.backend.plugin.pipeline.responseCodes.GarudaPipelineResponseCode;
import jp.sbi.garuda.backend.ui.GarudaGlassPanel;
import rone.backend.garudahandler.DiscoveryTouple;
import rone.filemanager.FileManager;
import rone.filemanager.Table;
import rone.ui.ImportDataDialog;
import rone.ui.MainWindow;
import rone.ui.DataTable;

public class GarudaHandler {

	private GarudaBackend mGarudaBackend;
	private JFrame mParentFrame;
	private GarudaBackendPipelinePlugin mPipelinePlugin;
	private DataTable mToxicologyTable;
	
	
	public GarudaHandler(MainWindow mainWindow) throws GarudaConnectionNotInitializedException, NetworkConnectionException
	{
		//this.mParentFrame = mainWindow;
		//this.mToxicologyTable = geneTable;
		
		mGarudaBackend = new GarudaBackend(GarudaConstants.GARUDA_ID, GarudaConstants.GARUDA_NAME, this.mParentFrame);
		
		mGarudaBackend.addGarudaGlassPanel(this.mParentFrame, null);
		
		initGarudaListeners();
		initPipeline();
		
		mGarudaBackend.activateGadget();

	
	}

	private void initPipeline () 
	{
		mPipelinePlugin = new GarudaBackendPipelinePlugin() ;
		
		mGarudaBackend.registerProtocolHandlerPlugin(mPipelinePlugin);
		
	}
	
	public boolean isConnected() {
		return mGarudaBackend.isConnectionLive();
	}
	
	public void activateConnection() 
	{
		mGarudaBackend.activateGadget();
	}
	
	public GarudaGlassPanel getGarudaGlassPanel () 
	{
		return mGarudaBackend.getGarudaGlassPanel() ;
	}
	
	public IncomingRequestProtocolHandler accessIncomingRequestListenerHandler (  )
	{
		return mGarudaBackend.getIncomingRequestHandler() ;
	}
	
	public IncomingRequestProtocolHandler accessIncomingResponseListenerHandler (  )
	{
		return mGarudaBackend.getIncomingRequestHandler() ;
	}
	
	public GarudaBackendPipelinePlugin accessPipelinePlugin () 
	{
		return mPipelinePlugin ;
	}
	
	public void garudaDiscover(File fileToDiscover, String fileFormat)
	{
		if ( fileToDiscover == null || !fileToDiscover.exists())
			throw new IllegalArgumentException( "File does not exist") ;
		if ( fileFormat == null || fileFormat.equals(""))
			throw new IllegalArgumentException( "Illegal file format.") ;
		
		
		mGarudaBackend.getCompatibleGadgetList(fileToDiscover, fileFormat);
	}
	
	public void garudaDiscover (DiscoveryTouple discoveryTouple)
	{
		if ( discoveryTouple == null || discoveryTouple.getFileFormat() == null || discoveryTouple.getFileToDiscover()== null)
			throw new IllegalArgumentException( "Incompatible discovery touple") ;
		
		mGarudaBackend.getCompatibleGadgetList(discoveryTouple.getFileToDiscover(), discoveryTouple.getFileFormat());
	}
	
	public void sendFileTo (CompatibleGadgetDetails targetGadget, boolean isStream) throws NoFileToSendException
	{
		if (!isStream)
			mGarudaBackend.sendDataToGadgetAsFile(targetGadget);
		else
			mGarudaBackend.sendDataToGadgetAsStream(targetGadget);
	}
	
	public void sendPipelineReply (File pipelineFile, String fileFormat) throws JsonGenerationException, JsonMappingException, IOException, PipelineNotInitializedException
	{
		mPipelinePlugin.sendSendPipelineMessageToGadgetResponse(pipelineFile, fileFormat);
	}
	
	public void sendPipelineOperationFailed (GarudaPipelineResponseCode pipelineResponseCode ) throws JsonGenerationException, JsonMappingException, IOException, PipelineNotInitializedException
	{
			mPipelinePlugin.sendPipelineFailedToGadgetResponse(pipelineResponseCode);
	}
	
	private void loadTable(Table incomingTable, String fromWhere) {
		
		if(mToxicologyTable.isEmpty()) {
			mToxicologyTable.setTable(incomingTable);
		} else {
			
			int inc_len = incomingTable.getIdentifiers().size();
			String[] inc_id = incomingTable.getIdentifiers().toArray(new String[inc_len]);
			
			int tox_len = mToxicologyTable.getIdentifiers().size();
			String[] tox_id = mToxicologyTable.getIdentifiers().toArray(new String[tox_len]);
			
			ImportDataDialog importSelection = new ImportDataDialog(mParentFrame, fromWhere, tox_id, inc_id) ;
			importSelection.setVisible(true);	
			
			String[] data = importSelection.getData();
			
			if(data[0] != null) {
				String keyTox = data[0];
				String keyInc = data[1];
				mToxicologyTable.importTable(keyTox, keyInc, incomingTable);
			}
			
		}
		
		
	}
	
	private void initGarudaListeners () {
				
		mGarudaBackend.getIncomingRequestHandler().addLoadDataRequestActionListener(new LoadDataRequestActionListener() {

			@Override
			public void loadDataRequestReceivedAsFile(File file, String senderId, String senderName,
					String originDeviceId, String currentDeviceId, String fileName, String fileFormat) {
				// TODO Auto-generated method stub
				System.out.print("loadDataRequestReceivedAsFile!");
				
				switch (senderName) {
					// TODO: maybe switch to senderId
					case "GeneMapper":
						
					try {
						Table shoeTable = FileManager.loadDataFile(file, ",");
						ArrayList<String> s = shoeTable.getIdentifiers();
						for(int i = 0; i < s.size(); i++) {
							s.set(i,  	"(S) " + s.get(i));
						}
						loadTable(shoeTable, "SHOE");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					case "Reactome gadget":
					
						try {
							Table reactomeTable = FileManager.loadDataFile(file, ",");
							loadTable(reactomeTable, "Reactome");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						break;
						
						
				}
				
			}

			@Override
			public void loadDataRequestReceivedAsStream(byte[] receivedData, String senderId, String senderName,
					String originDeviceId, String currentDeviceId, String fileName, String fileFormat) {
				// TODO Auto-generated method stub
				System.out.print("loadDataRequestReceivedAsStream!");
			}
		});
		
		mGarudaBackend.getIncomingResponseHandler().addActivateGadgetResponseActionListener(new ActivateGadgetResponseActionListener() {
			
			@Override
			public void gadgetActivationFailed(GarudaResponseCode response) {
				JOptionPane.showMessageDialog(mParentFrame, "Activation Failed " + response.toString(), "Garuda Error", JOptionPane.ERROR_MESSAGE);
				
			}
			
			@Override
			public void gadgetActivated() {
				// JOptionPane.showMessageDialog(parentFrame, "Connected to Garuda ", "Garuda connection", JOptionPane.INFORMATION_MESSAGE);
			}
		}) ;
		
		mGarudaBackend.getIncomingResponseHandler().addGetCompatibleGadgetListResponseActionListener(new GetCompatibleGadgetListResponseActionListener() {
			
			@Override
			public void noCompatibleGadgetsFound(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(mParentFrame, "No compatible Gadgets " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
				
			}
			
			@Override
			public void gotErrorOnCompatibleGadgetListRequest(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(mParentFrame, "Error while getting compatible gadgets " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
			}
			
			@Override
			public void gotCompatibleGadgetList(List<CompatibleGadgetDetails> gadgetList) {
				
				getGarudaGlassPanel().showPanel(gadgetList);
				
			}
			
			@Override
			public void fileNotInOutBoundList(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(mParentFrame, "File not in output list " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
			}
		}) ;
	
	}
	
}
