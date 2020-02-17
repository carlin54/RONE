package toxicologygadget.backend.garudahandler;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
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
import toxicologygadget.backend.garudahandler.DiscoveryTouple;
import toxicologygadget.filemanager.Database;
import toxicologygadget.filemanager.FileManager;
import toxicologygadget.ui.ToxicologyTable;

public class GarudaHandler {

	private GarudaBackend garudaBackend;
	private JFrame parentFrame;
	private GarudaBackendPipelinePlugin pipelinePlugin;
	private ToxicologyTable toxicologyTable;
	
	
	public GarudaHandler (JFrame parentFrame, ToxicologyTable geneTable) throws GarudaConnectionNotInitializedException, NetworkConnectionException
	{
		this.parentFrame = parentFrame;
		this.toxicologyTable = geneTable;
		
		garudaBackend = new GarudaBackend(GarudaConstants.GARUDA_ID, GarudaConstants.GARUDA_NAME, this.parentFrame);

		garudaBackend.addGarudaGlassPanel(this.parentFrame, null);
		
		initGarudaListeners();
		initPipeline();
		
		garudaBackend.activateGadget();
		
		garudaBackend.getCompatibleGadgetList("txt", "genelist");
		
		
		//File file = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\EnsembleGenelist2.txt");
		//garudaDiscover(file, "ensemble");
	
	}

	private void initPipeline () 
	{
		pipelinePlugin = new GarudaBackendPipelinePlugin() ;
		
		garudaBackend.registerProtocolHandlerPlugin(pipelinePlugin);
		
	}
	
	public boolean isConnected() {
		return garudaBackend.isConnectionLive();
	}
	
	public void activateConnection() 
	{
		garudaBackend.activateGadget();
	}
	
	public GarudaGlassPanel getGarudaGlassPanel () 
	{
		return garudaBackend.getGarudaGlassPanel() ;
	}
	
	public IncomingRequestProtocolHandler accessIncomingRequestListenerHandler (  )
	{
		return garudaBackend.getIncomingRequestHandler() ;
	}
	
	public IncomingRequestProtocolHandler accessIncomingResponseListenerHandler (  )
	{
		return garudaBackend.getIncomingRequestHandler() ;
	}
	
	public GarudaBackendPipelinePlugin accessPipelinePlugin () 
	{
		return pipelinePlugin ;
	}
	
	public void garudaDiscover(File fileToDiscover, String fileFormat)
	{
		if ( fileToDiscover == null || !fileToDiscover.exists())
			throw new IllegalArgumentException( "File does not exist") ;
		if ( fileFormat == null || fileFormat.equals(""))
			throw new IllegalArgumentException( "Illegal file format.") ;
		
		
		garudaBackend.getCompatibleGadgetList(fileToDiscover, fileFormat);
	}
	
	public void garudaDiscover (DiscoveryTouple discoveryTouple)
	{
		if ( discoveryTouple == null || discoveryTouple.getFileFormat() == null || discoveryTouple.getFileToDiscover()== null)
			throw new IllegalArgumentException( "Incompatible discovery touple") ;
		
		garudaBackend.getCompatibleGadgetList(discoveryTouple.getFileToDiscover(), discoveryTouple.getFileFormat());
	}
	
	public void sendFileTo (CompatibleGadgetDetails targetGadget, boolean isStream) throws NoFileToSendException
	{
		if (!isStream)
			garudaBackend.sendDataToGadgetAsFile(targetGadget);
		else
			garudaBackend.sendDataToGadgetAsStream(targetGadget);
	}
	
	public void sendPipelineReply (File pipelineFile, String fileFormat) throws JsonGenerationException, JsonMappingException, IOException, PipelineNotInitializedException
	{
		pipelinePlugin.sendSendPipelineMessageToGadgetResponse(pipelineFile, fileFormat);
	}
	
	public void sendPipelineOperationFailed (GarudaPipelineResponseCode pipelineResponseCode ) throws JsonGenerationException, JsonMappingException, IOException, PipelineNotInitializedException
	{
			pipelinePlugin.sendPipelineFailedToGadgetResponse(pipelineResponseCode);
	}
	
	private void initGarudaListeners () {
				
		garudaBackend.getIncomingRequestHandler().addLoadDataRequestActionListener(new LoadDataRequestActionListener() {

			@Override
			public void loadDataRequestReceivedAsFile(File file, String senderId, String senderName,
					String originDeviceId, String currentDeviceId, String fileName, String fileFormat) {
				// TODO Auto-generated method stub
				System.out.print("loadDataRequestReceivedAsFile!");
				
				switch (senderName) {
					// TODO: maybe switch to senderId
					case "GeneMapper": 
						Database shoeTable = FileManager.loadCSV(file);
						toxicologyTable.importTable(shoeTable);
						
						break;
					case "Reactome gadget":
						Database reactomeData = FileManager.loadCSV(file);
						
						
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
		
		garudaBackend.getIncomingResponseHandler().addActivateGadgetResponseActionListener(new ActivateGadgetResponseActionListener() {
			
			@Override
			public void gadgetActivationFailed(GarudaResponseCode response) {
				JOptionPane.showMessageDialog(parentFrame, "Activation Failed " + response.toString(), "Garuda Error", JOptionPane.ERROR_MESSAGE);
				
			}
			
			@Override
			public void gadgetActivated() {
				// JOptionPane.showMessageDialog(parentFrame, "Connected to Garuda ", "Garuda connection", JOptionPane.INFORMATION_MESSAGE);
			}
		}) ;
		
		garudaBackend.getIncomingResponseHandler().addGetCompatibleGadgetListResponseActionListener(new GetCompatibleGadgetListResponseActionListener() {
			
			@Override
			public void noCompatibleGadgetsFound(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(parentFrame, "No compatible Gadgets " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
				
			}
			
			@Override
			public void gotErrorOnCompatibleGadgetListRequest(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(parentFrame, "Error while getting compatible gadgets " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
			}
			
			@Override
			public void gotCompatibleGadgetList(List<CompatibleGadgetDetails> gadgetList) {
				
				getGarudaGlassPanel().showPanel(gadgetList);
				
			}
			
			@Override
			public void fileNotInOutBoundList(GarudaResponseCode responseCode) {
				JOptionPane.showMessageDialog(parentFrame, "File not in output list " + responseCode.id, "Garuda Error", JOptionPane.ERROR_MESSAGE);
			}
		}) ;
	
	}
	
}
