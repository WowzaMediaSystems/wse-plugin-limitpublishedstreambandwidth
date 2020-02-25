# LimitPublishedStreamBandwidth
The **ModuleLimitPublishedStreamBandwidth** module for [Wowza Streaming Engine™ media server software](https://www.wowza.com/products/streaming-engine) enables you to automatically disconnect RTMP live sources that exceed a set bandwidth rate limit.

This repo includes a [compiled version](/lib/wse-plugin-limitedpublishedstreambandwidth.jar).

## Prerequisites
Wowza Streaming Engine 4.0.0 or later is required.

## Usage
The **ModuleLimitPublishedStreamBandwidth** module enables you to configure a maximum bitrate for an application. When an RTMP stream is published to the configured application, it's monitored continuously to ensure its bitrate stays within the set limit. If the stream bitrate exceeds the limit, the RTMP source is disconnected.

## More resources
To use the compiled version of this module, see [Monitor bandwidth of published streams with a Wowza Streaming Engine Java module](https://www.wowza.com/docs/how-to-monitor-bandwidth-of-published-streams-modulelimitpublishedstreambandwidth).

[Wowza Streaming Engine Server-Side API Reference](https://www.wowza.com/resources/serverapi/)

[How to extend Wowza Streaming Engine using the Wowza IDE](https://www.wowza.com/docs/how-to-extend-wowza-streaming-engine-using-the-wowza-ide)

Wowza Media Systems™ provides developers with a platform to create streaming applications and solutions. See [Wowza Developer Tools](https://www.wowza.com/resources/developers) to learn more about our APIs and SDK.

## Contact
[Wowza Media Systems, LLC](https://www.wowza.com/contact)

## License
This code is distributed under the [Wowza Public License](https://github.com/WowzaMediaSystems/wse-plugin-limitpublishedstreambandwidth/blob/master/LICENSE.txt).
