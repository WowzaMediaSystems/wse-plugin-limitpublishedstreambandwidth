/**
 * Wowza server software and all components Copyright 2006 - 2015, Wowza Media Systems, LLC, licensed pursuant to the Wowza Media Software End User License Agreement.
 */
package com.wowza.wms.plugin;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.wowza.util.IOPerformanceCounter;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.application.WMSProperties;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.logging.WMSLoggerIDs;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.rtp.model.RTPSession;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.IMediaStreamActionNotify;

public class LimitPublishedStreamBandwidth extends ModuleBase implements IMediaStreamActionNotify
{
	private class MonitorStream
	{
		public Timer mTimer;
		public TimerTask mTask;
		public IMediaStream stream;
	
		public MonitorStream(IMediaStream s)
		{
			stream = s;
			mTask = new TimerTask()
			{
				public void run()
				{
	
					if (stream == null)
						stop();
	
					IOPerformanceCounter perf = stream.getMediaIOPerformance();
					Double bitrate = perf.getMessagesInBytesRate() * 8 * .001;
	
					if (debugLog)
						logger.info(MODULE_NAME + ".MonitorStream.run '" + stream.getName() + "' BitRate: " + Math.round(Math.floor(bitrate)) + "kbs, MaxBitrate:" + maxBitrate, WMSLoggerIDs.CAT_application, WMSLoggerIDs.EVT_comment);
	
					if (bitrate > maxBitrate && maxBitrate > 0)
					{
						
						if(stream.getClient() != null){
							logger.info(MODULE_NAME + ".MonitorStream.run[RTMP] Sent NetStream.Publish.Rejected to " + stream.getClientId() + " stream name: " + stream.getName() + ", BitRate: " + Math.round(Math.floor(bitrate)) + "kbs", WMSLoggerIDs.CAT_application, WMSLoggerIDs.EVT_comment);
							sendStreamOnStatusError(stream, "NetStream.Publish.Rejected", "bitrate too high: " + Math.round(Math.floor(bitrate)) + "kbs");
							stream.getClient().setShutdownClient(true);
						}
						else if(stream.getRTPStream() != null){
							logger.info(MODULE_NAME + ".MonitorStream.run[RTSP] Shutdown session: " + stream.getRTPStream().getSession() + " stream name: " + stream.getName() + ", BitRate: " + Math.round(Math.floor(bitrate)) + "kbs", WMSLoggerIDs.CAT_application, WMSLoggerIDs.EVT_comment);
							stream.getRTPStream().getRTPContext().shutdownRTPSession(stream.getRTPStream().getSession());							
						}
					}
				}
			};
			mTimer = new Timer();
		}
	
		public void start()
		{
			if (mTimer == null)
				mTimer = new Timer();
			mTimer.scheduleAtFixedRate(mTask, new Date(), interval);
		}
	
		public void stop()
		{
			if (mTimer != null)
			{
				mTimer.cancel();
				mTimer = null;
			}
		}
	}

	public static final String MODULE_NAME = "ModuleLimitPublishedStreamBandwidth";
	public static final String PROP_NAME_PREFIX = "limitPublishedStreamBandwidth";
	
	IApplicationInstance appInstance;
	int maxBitrate = 800; // 0 = no limit
	int sustained = 10;
	int interval = 5000;
	boolean debugLog = false;
	
	WMSLogger logger = null;

	public void onAppStart(IApplicationInstance appInstance)
	{
		this.appInstance = appInstance;
		this.logger = WMSLoggerFactory.getLoggerObj(appInstance);
		
		// old prop name
		maxBitrate = appInstance.getProperties().getPropertyInt("MaxBitrate", maxBitrate);
		// new prop name
		maxBitrate = appInstance.getProperties().getPropertyInt(PROP_NAME_PREFIX + "MaxBitrate", maxBitrate);
		
		// old prop name
		debugLog = appInstance.getProperties().getPropertyBoolean("StreamMonitorLogging", debugLog);
		// new prop name
		debugLog = appInstance.getProperties().getPropertyBoolean(PROP_NAME_PREFIX + "DebugLog", debugLog);
		if(logger.isDebugEnabled())
			debugLog = true;
		
		sustained = appInstance.getProperties().getPropertyInt(PROP_NAME_PREFIX + "Sustained", sustained); // not used
		
		logger.info(MODULE_NAME + " MaxBitrate: " + maxBitrate, WMSLoggerIDs.CAT_application, WMSLoggerIDs.EVT_comment);
	}

	public void onStreamCreate(IMediaStream stream)
	{
		stream.addClientListener(this);
	}

	public void onStreamDestroy(IMediaStream stream)
	{
		stream.removeClientListener(this);
	}

	public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
	{
		if(stream.getClient() == null)
			return;
		
		WMSProperties props = stream.getProperties();

		MonitorStream monitor;

		synchronized(props)
		{
			monitor = (MonitorStream)props.get("monitor");
		}
		if (monitor != null)
			monitor.stop();
	}

	public void onPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
	{
		if (stream.getClient() == null && stream.getRTPStream() == null)
		{
			logger.warn(MODULE_NAME + ".onPublish: Stream is being published by a non-rtmp connection. It is not possible to limit the bandwidth. [" + appInstance.getContextStr() + "/" + streamName + "]", WMSLoggerIDs.CAT_application, WMSLoggerIDs.EVT_comment);
			return;
		}
		
		if(stream.getRTPStream() != null)
		{
			RTPSession session = stream.getRTPStream().getSession();
			if(session == null || !session.isAnnounce())
			{
				logger.warn(MODULE_NAME + ".onPublish: Stream is being published by a RTSP mediaCaster connection. It is not possible to limit the bandwidth. [" + appInstance.getContextStr() + "/" + streamName + "]", WMSLoggerIDs.CAT_application, WMSLoggerIDs.EVT_comment);
				return;
			}
		}
		
		MonitorStream monitor = new MonitorStream(stream);
		WMSProperties props = stream.getProperties();
		synchronized(props)
		{
			props.put("monitor", monitor);
		}
		monitor.start();
	}

	public void onPlay(IMediaStream stream, String streamName, double playStart, double playLen, int playReset)
	{
	}

	public void onSeek(IMediaStream stream, double location)
	{
	}

	public void onStop(IMediaStream stream)
	{
	}

	public void onPause(IMediaStream stream, boolean isPause, double location)
	{
	}
}