# LimitPublishedStreamBandwidth
The ModuleLimitPublishedStreamBandwidth module for [Wowza Streaming Engine™ media server software](https://www.wowza.com/products/streaming-engine) automatically disconnects RTMP sources that exceed a set bandwidth rate limit.

## Prerequisites

Wowza Streaming Engine 4.0.0 or later is required.

## Usage

The ModuleLimitPublishedStreamBandwidth module enables you to configure a maximum bitrate for an application. When an RTMP stream is published to the configured application, it's bitrate is continuously monitored to ensure it stays within the set limit. If the stream bitrate exceeds the limit, it is disconnected.

## More resources

[Wowza Streaming Engine Server-Side API Reference](https://www.wowza.com/resources/WowzaStreamingEngine_ServerSideAPI.pdf)

[How to extend Wowza Streaming Engine using the Wowza IDE](https://www.wowza.com/forums/content.php?759-How-to-extend-Wowza-Streaming-Engine-using-the-Wowza-IDE)

Wowza Media Systems™ provides developers with a platform to create streaming applications and solutions. See [Wowza Developer Tools](https://www.wowza.com/resources/developers) to learn more about our APIs and SDK.

To use the compiled version of this module, see [How to monitor bandwidth of published streams(ModuleLimitPublishedStreamBandwidth)](https://www.wowza.com/forums/content.php?231-How-to-monitor-bandwidth-of-published-streams-(ModuleLimitPublishedStreamBandwidth)).

## Contact

[Wowza Media Systems, LLC](https://www.wowza.com/contact)

## License

This code is distributed under the [Wowza Public License](https://github.com/WowzaMediaSystems/wse-plugin-limitpublishedstreambandwidth/blob/master/LICENSE.txt).
