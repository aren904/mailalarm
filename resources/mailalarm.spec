Name: mailalarm			
Version: 2.0.0
Release: 41%{?dist}
Summary: mailalarm for streamer
Group: System Environment/Daemons
License: Commercial
Source:	%{name}.tar.gz
BuildRoot: %(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)

#BuildRequires: java-1.8.0-openjdk-devel >= 1.8.0
#Requires: java-1.8.0-openjdk >= 1.8.0

%description

%prep
%define __strip /bin/true
%define __os_install_post %{nil}
%setup -n %{name}

%install
rm -rf %{buildroot}
%define _mailalarmhome /usr/local/mailalarm
%define _initddir /usr/lib/systemd/system

mkdir -p %{buildroot}%{_mailalarmhome}
mkdir -p %{buildroot}%{_initddir}

install -m 644 mailalarm-0.1.1.jar %{buildroot}%{_mailalarmhome}
install -m 755 init/mailalarm.service %{buildroot}%{_initddir}
install -m 644 log4j.properties %{buildroot}%{_mailalarmhome}

install -m 644 application.properties %{buildroot}%{_mailalarmhome}

install -m 644 druid.properties %{buildroot}%{_mailalarmhome}



%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)
%{_mailalarmhome}/mailalarm-0.1.1.jar
%{_mailalarmhome}/log4j.properties

%{_mailalarmhome}/application.properties
%{_mailalarmhome}/druid.properties

%{_initddir}/mailalarm.service

%pre
#check port 7082
lsof -i:7082
if [ $? -eq 0 ];then
echo "7082 port is occupied!"
exit -1
fi

%post
systemctl daemon-reload
systemctl stop mailalarm.service
if [ "$1" = "1" ]; then
   systemctl enable mailalarm.service
   systemctl start mailalarm.service
else
   systemctl stop mailalarm.service
   systemctl disable mailalarm.service
fi
echo "mailalarm is installed successfully."

%preun
if [ "$1" = "0" ]; then
systemctl stop mailalarm.service
fi

%postun
rm -rf %{_mailalarmhome}
rm -rf %{_initddir}/mailalarm.service

%changelog
