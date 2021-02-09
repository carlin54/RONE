package rone.backend.garudahandler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
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
	private MainWindow mMainWindow;
	
	public GarudaHandler(MainWindow mainWindow) throws GarudaConnectionNotInitializedException, NetworkConnectionException
	{
		this.mMainWindow = mainWindow;
		this.mParentFrame = mMainWindow.getFrame();
		
		mGarudaBackend = new GarudaBackend(GarudaConstants.GARUDA_ID, GarudaConstants.GARUDA_NAME, mParentFrame);
		
		mGarudaBackend.addGarudaGlassPanel(mParentFrame, null);
		
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
		return mPipelinePlugin;
	}
	
	public void garudaDiscover(File fileToDiscover, String fileFormat)
	{
		if ( fileToDiscover == null || !fileToDiscover.exists())
			throw new IllegalArgumentException("File does not exist") ;
		if ( fileFormat == null || fileFormat.equals(""))
			throw new IllegalArgumentException("Illegal file format.") ;
		
		
		mGarudaBackend.getCompatibleGadgetList(fileToDiscover, fileFormat);
	}
	
	public void garudaDiscover(DiscoveryTouple discoveryTouple)
	{
		if ( discoveryTouple == null || discoveryTouple.getFileFormat() == null || discoveryTouple.getFileToDiscover()== null)
			throw new IllegalArgumentException( "Incompatible discovery touple") ;
		
		mGarudaBackend.getCompatibleGadgetList(discoveryTouple.getFileToDiscover(), discoveryTouple.getFileFormat());
	}
	
	public void sendFileTo(CompatibleGadgetDetails targetGadget, boolean isStream) throws NoFileToSendException
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
	
	private String[] getColumnHeaders(File file, String senderName) {
		String[] columnHeaders = null;
		switch(senderName) {
		case "GeneMapper": 
			break;
			
		case "Reactome gadget": 
			
			break;
			
		case default: 
			
			break;
		}
		
		
		
		return columnHeaders;
	}
	
	private void initGarudaListeners () {
				
		mGarudaBackend.getIncomingRequestHandler().addLoadDataRequestActionListener(new LoadDataRequestActionListener() {

			@Override
			public void loadDataRequestReceivedAsFile(
					File file, 
					String senderId, 
					String senderName,
					String originDeviceId, 
					String currentDeviceId, 
					String fileName, 
					String fileFormat) {
				
				if(!file.exists())
					return;
				
				
				// TODO Auto-generated method stub
				System.out.print("loadDataRequestReceivedAsFile!");
				
				String tableName = senderName + " (" + fileFormat + ")"; 
				
				
				
				ArrayList<Object[]> loadedFile = null; 
				String[] columnHeaders = null;
				try {
					switch(senderName) {
					
					case "Reactome gadget":
						columnHeaders = new String[]{"Pathway", "Species", "Coverage %", "pval", "FDR"};
						loadedFile = FileManager.loadStructuredFile(file, ",");
						break;
					
					case "GeneMapper":
						columnHeaders = new String[]{"Gene", "NM", "TF", "Region", "Strand", "MA Score", "PSSM Score", "ID", "Motif", "Consensus", "Similarity", "Pareto"};
						loadedFile = FileManager.loadStructuredFile(file, ",");
						break;
						
					default:
						
							String path = file.getAbsolutePath();
							String extension = FilenameUtils.getExtension(path);
							
							switch(extension) {
								case "csv": 
									columnHeaders = null;
									loadedFile = FileManager.loadCSV(file, ",", true);
									break; 
								
								case "txt": 
									columnHeaders = new String[1];
									columnHeaders[0] = fileFormat;
									loadedFile = FileManager.loadTextFile(file, true);
									break; 
								
								default:
									columnHeaders = new String[1];
									columnHeaders[0] = senderName;
									loadedFile = FileManager.loadTextFile(file, true);
									break; 
							}
					}
					
					mMainWindow.getTabbedPane().addTab(tableName, columnHeaders, null, loadedFile);
				
				} catch (IOException | SQLException e) {
					MainWindow.showError(e);
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
