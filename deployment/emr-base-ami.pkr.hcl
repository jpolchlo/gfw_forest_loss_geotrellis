packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.2"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

source "amazon-ebs" "ubuntu" {
  ami_name      = "gdal-3.1.2-linux-aws-micromamba"
  instance_type = "t2.micro"
  region        = "us-east-1"
  source_ami_filter {
    filters = {
      name                               = "al2023-ami-2023*"
      root-device-type                   = "ebs"
      virtualization-type                = "hvm"
      "block-device-mapping.volume-type" = "gp3",
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
      "curl -Ls https://micro.mamba.pm/api/micromamba/linux-64/latest | tar -xvj bin/micromamba",
      "sudo mv bin/micromamba /usr/local/bin",
      "micromamba shell init -s bash -p ~/micromamba/",
      "echo \"micromamba activate\" >> ~/.bashrc",
      "echo \"export LD_LIBRARY_PATH=/home/ec2-user/micromamba/lib/:/usr/local/lib:/usr/lib/hadoop/lib/native:/usr/lib/hadoop-lzo/lib/native:/docker/usr/lib/hadoop/lib/native:/docker/usr/lib/hadoop-lzo/lib/native:/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib\" >> ~/.bashrc",
      "echo \"export PATH=/home/ec2-user/micromamba/bin:\\$PATH\" >> ~/.bashrc",
      "source ~/.bashrc",
      "micromamba install -c conda-forge -y python=3.6",
      "micromamba install -c anaconda -y hdf5",
      "micromamba install -c conda-forge -y libnetcdf gdal=3.1.2",
      "pip3 install tqdm"
    ]
  }
}
