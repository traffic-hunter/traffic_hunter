![Traffic Hunter_Logo_2_배경](https://github.com/user-attachments/assets/43779ef6-f4f8-40f5-98b5-447754bfd2a4)
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

## Latest Release 🚀

The pre-release version is v1.0.0. 

- 2024/12/30 - 1.0.0 version

### Notes

- Agent that is v1.0.0 supports only `spring framework 6.x` and `boot 3.x`

## Intro. Traffic-Hunter

**Traffic-Hunter** is APM (Application Performance Management) 


- `Real-Time Monitoring`: Capture and visualize application metrics and transaction logs in real-time.


- `Easy Configuration`: Easily configure the agent using a simple YAML file, making setup straightforward and user-friendly.


- `Efficient Data Storage`: Leverages [**TimescaleDB**](https://www.timescale.com/) for efficient storage and querying of time-series data, 
   enabling high-performance analytics on metrics and traces.


- `Visualization`: This APM leverages [**Grafana**](https://grafana.com/docs/grafana/latest/) for powerful and customizable visualization of metrics and transaction logs, enabling real-time insights and easy integration into your observability stack.


- `Zero-Code`: Traffic Hunter Agent uses **Java Instrumentation** to modify bytecode at runtime, requiring no code changes from users. Simply attach the agent, and it automatically tracks metrics and traces with zero configuration.

## Getting Started

- [**Quick-start**](https://traffic-hunter.gitbook.io/traffic-hunter) - Quick-start and installation guide.

## Overview 👀

- **Application Health Check** - cpu, heap memory, thread, web server, dbcp (database connection pool)

<img width="1440" alt="스크린샷 2024-12-12 오후 10 52 04" src="https://github.com/user-attachments/assets/18866da9-8b64-4ea8-a703-18e23b61d756" />

- **Transaction Logs** - Transaction logs track application requests in real-time, recording executed methods and related information.

<img width="1421" alt="스크린샷 2024-12-12 오후 10 55 37" src="https://github.com/user-attachments/assets/35b09f7e-caf2-4d58-9912-3efefd56bb28" />

## Version Compatibility 👏

| Java Version | Agent           | Server         | Notes                                     |
|--------------|-----------------|----------------|-------------------------------------------|
| 21           | ✅ Supported     | ✅ Supported   | Includes support for advanced features like virtual threads |

### Notes
- Java versions below 21 are **not supported**.
- For optimal performance and access to the latest features, use Java 21.
- Ensure both the agent and the server are running compatible Java versions.

## License

```text
The MIT License

Copyright (c) 2024 yungwang-o

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```





## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/JuSeong1130"><img src="https://avatars.githubusercontent.com/u/53209324?v=4?s=100" width="100px;" alt="JuSeong1130"/><br /><sub><b>JuSeong1130</b></sub></a><br /><a href="https://github.com/traffic-hunter/traffic_hunter/commits?author=JuSeong1130" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!