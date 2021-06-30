#!/bin/bash
######################
# Author: Long Tran
######################

workspace_dir='/var/lib/jenkins/workspace'
build_dir='build'
built_date=''

funInit() {
	echo 'Starting deploy...'
	cd $workspace_dir
	#Kiểm tra thư mục build nếu chưa có thì create
	if [ ! -d "$build_dir" ]; then
		sudo mkdir $build_dir
	fi
}

funBuildGradle() {
	# Đi điến folder project
	cd $workspace_dir/$project_name
	# cmod file gralde build  
	sudo chmod +x gradlew
	echo 'Cleaning project...'
	# clean project
	sudo ./gradlew clean
	# kiểm tra kết quả clean gradle
	if [ $? -eq 0 ]; then
	   echo 'Clean project success!' 
	else
	   echo 'Clean project failed!'
	   exit 1
	fi
	echo 'Built stating...'
	# build project và skip test
	sudo ./gradlew build -x test
	if [ $? -eq 0 ]; then
	   echo 'Build project success!' 
	else
	   echo 'Build project failed!'
	   exit 1
	fi
	# tìm file jar
	local jar_file=$(find build/libs/ -type f -name "*.jar")
	# kiểm tra và tạo thư mục build
	built_date=$(date +%Y-%m-%d-%H%M%S)
	if [ ! -d "$built_date" ]; then
		sudo mkdir $workspace_dir/$build_dir/$built_date
	fi
	echo 'Copying Jar file...'
	# copy file jar vào thư mục build
	sudo cp $jar_file $workspace_dir/$build_dir/$built_date/
}

funCreateFile() {
# tạo file service systemd
sudo sh -c "cat >>$service_file" <<-EOL
[Unit]
Description=Spring app
After=syslog.target
After=network.target[Service]
User=username
Type=simple
[Service]
ExecStart=/usr/bin/java -jar $execute_file
Restart=always
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=demojenkins
[Install]
WantedBy=multi-user.target
EOL
}

funCreateService() {
	echo 'Creating service...'
	# đi đến folder build
	cd $workspace_dir/$build_dir/$built_date/
	# tìm file jar
	execute_file=$(find $workspace_dir/$build_dir/$built_date -type f -name "*.jar")
	# create service với file jar được chỉ định
	funCreateFile
	service_path=/etc/systemd/system/$service_file
	# stop service trước khi copy service
	sudo systemctl stop $service_name
	if [ -f "$service_path" ]; then
		# delete service 
		sudo rm -f /etc/systemd/system/$service_file
	fi
	echo 'Copying service file...'
	# copy service vào thư mục hệ thống
	sudo cp $service_file /etc/systemd/system/
}

funHelp() {
	echo ""
	echo "Usage: $0 -p project-name"
	echo -p "\t-p project name "
	exit 1
}

funRunService() {
	echo "Starting $service_name service..."
	# reload lại systemd
	sudo systemctl daemon-reload
	# start service
	sudo systemctl start $service_name
	# kiểm tra qua trình start thành công hay chưa
	if [ $? -eq 0 ]; then
	   echo "Start service $service_name success!"
	else
	   echo 'Start service failed!'
	   exit 1
	fi
	echo 'Deploy finish!'
}

main() {
	funInit
	funBuildGradle
	funCreateService
	funRunService
}

# lấy kết quả param truyền vào để xữ lý
while getopts "p:" opt
do
   case "$opt" in
      p ) project_name="$OPTARG" ;;
      ? ) funHelp ;;
   esac
done
# kiểm tra nếu chưa nhập project name thì thông báo 
if [ -z "$project_name" ]
then
   echo "Please set project name";
   funHelp
fi
service_name=$project_name
service_file=$service_name.service

main
