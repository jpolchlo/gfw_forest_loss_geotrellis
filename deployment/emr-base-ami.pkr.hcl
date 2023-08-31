packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.2"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

source "amazon-ebs" "ubuntu" {
  ami_name      = "gdal-3.1.2-linux-aws"
  instance_type = "t2.micro"
  region        = "us-east-1"
  source_ami_filter {
    filters = {
      name                               = "amzn2-ami-hvm-*"
      root-device-type                   = "ebs"
      virtualization-type                = "hvm"
      "block-device-mapping.volume-type" = "gp2",
      architecture                       = "x86_64",
    }
    most_recent = true
    owners      = ["amazon"]
  }
  ssh_username = "ec2-user"
  subnet_filter {
    filters = {
      "map-public-ip-on-launch": true
    }
    random = true
  }
  ssh_interface = "public_ip"
  associate_public_ip_address = true
}

build {
  name = "learn-packer"
  sources = [
    "source.amazon-ebs.ubuntu"
  ]

  provisioner "shell" {
    environment_vars = [
      "PROJ_VERSION=6.1.1",
      "GDAL_VERSION=3.1.2",
    ]
    inline = [
      "sudo yum install gcc-c++.x86_64 cpp.x86_64 sqlite-devel.x86_64 libtiff.x86_64 cmake3.x86_64 -y",
      "cd /tmp",
      "wget https://download.osgeo.org/proj/proj-$PROJ_VERSION.tar.gz",
      "tar -xvf proj-$PROJ_VERSION.tar.gz",
      "cd proj-$PROJ_VERSION",
      "./configure",
      "make",
      "sudo make install",
      "cd /tmp",
      "rm -rf proj-$PROJ_VERSION",
      "wget https://github.com/OSGeo/gdal/releases/download/v$GDAL_VERSION/gdal-$GDAL_VERSION.tar.gz",
      "tar -xvf gdal-$GDAL_VERSION.tar.gz",
      "cd gdal-$GDAL_VERSION",
      "./configure --with-proj=/usr/local --with-python",
      "make",
      "sudo make install",
      "cd /tmp",
      "sudo rm -rf gdal-$GDAL_VERSION",
      "which gdalinfo",
      "gdalinfo --version"
    ]
  }
}
