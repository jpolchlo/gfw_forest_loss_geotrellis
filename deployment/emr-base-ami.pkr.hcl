packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.2"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

source "amazon-ebs" "ubuntu" {
  ami_name      = "gdal-3.1.2-linux-aws-conda"
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
      "cd /tmp",
      "wget https://repo.continuum.io/miniconda/Miniconda-latest-Linux-x86_64.sh",
      "sudo sh Miniconda-latest-Linux-x86_64.sh -b -p /usr/local/miniconda",
      "rm Miniconda-latest-Linux-x86_64.sh",
      "source ~/.bashrc",
      "export PATH=/usr/local/miniconda/bin:/usr/local/bin:$PATH",
      "conda config --add channels conda-forge",
      "sudo pip3 install tqdm",
      "sudo /usr/local/miniconda/bin/conda install python=3.6 -y",
      "sudo /usr/local/miniconda/bin/conda install -c anaconda hdf5 -y",
      "sudo /usr/local/miniconda/bin/conda install -c conda-forge libnetcdf gdal=$GDAL_VERSION -y",
      "echo \"export PATH=/usr/local/miniconda/bin:\\$PATH\" >> ~/.bashrc",
      "echo \"export LD_LIBRARY_PATH=/usr/local/miniconda/lib/:/usr/local/lib:/usr/lib/hadoop/lib/native:/usr/lib/hadoop-lzo/lib/native:/docker/usr/lib/hadoop/lib/native:/docker/usr/lib/hadoop-lzo/lib/native:/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib\" >> ~/.bashrc"
    ]
  }
}
