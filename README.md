# GaussianDispersion (work in progress)


Gaussian Dispersion to system służący do liczenia i wizualizowania maksymalnego rozprzestrzenienia się gazu. 
Wynik liczony jest na podstawie modelu gaussowskiego (http://personalpages.manchester.ac.uk/staff/paul.connolly/teaching/practicals/material/gaussian_plume_modelling/gp_notes.pdf) 

Używane technologie:
- Backend:
    - CUDA 9
    - Java 8
- Frontend:
    - Javascript ( głównie biblioteki React,  Leaflet)

Projekt testowany na komputerze:
###System:    Kernel: 4.13.0-43-generic x86_64 (64 bit gcc: 5.4.0) Desktop: KDE Plasma 5.5.5 (Qt 5.5.1)
           Distro: Ubuntu 16.04 xenial
Machine:   Mobo: MSI model: Z97-G43 (MS-7816) v: 3.0 Bios: American Megatrends v: V17.8 date: 12/24/2014
CPU:       Quad core Intel Core i5-4690K (-MCP-) cache: 6144 KB
           flags: (lm nx sse sse2 sse3 sse4_1 sse4_2 ssse3 vmx) bmips: 27998
           clock speeds: max: 3900 MHz 1: 1020 MHz 2: 1521 MHz 3: 2323 MHz 4: 2442 MHz
Graphics:  Card: NVIDIA GM204 [GeForce GTX 970] bus-ID: 01:00.0
           Display Server: X.Org 1.19.5 driver: nvidia Resolution: 1920x1080@59.93hz, 1280x1024@75.02hz
           GLX Renderer: GeForce GTX 970/PCIe/SSE2 GLX Version: 4.6.0 NVIDIA 396.26 Direct Rendering: Yes
Network:   Card-1: Realtek RTL8111/8168/8411 PCI Express Gigabit Ethernet Controller
           driver: r8169 v: 2.3LK-NAPI port: d000 bus-ID: 03:00.0
           Card-2: Realtek RTL-8100/8101L/8139 PCI Fast Ethernet Adapter
           driver: 8139too v: 0.9.28 port: c000 bus-ID: 05:01.0
Drives:    HDD Total Size: 1628.3GB (53.0% used) ID-1: /dev/sda model: ST3500620AS size: 500.1GB 
           ID-2: /dev/sdb model: ADATA_SP920SS size: 128.0GB 
           ID-3: USB /dev/sdc model: External_USB_3.0 size: 1000.2GB 
           
CUDA: nvcc: NVIDIA (R) Cuda compiler driver
Copyright (c) 2005-2015 NVIDIA Corporation
Built on Tue_Aug_11_14:27:32_CDT_2015
Cuda compilation tools, release 7.5, V7.5.17


