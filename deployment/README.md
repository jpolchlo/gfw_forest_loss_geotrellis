# Packer script for building EMR AMI

To generate, make sure your AWS profile is set for the correct account, then run
```bash
packer build emr-ase-ami.pkr.hcl
```

## Notes

This script has not been tested and needs some work.  GDAL version should be used in the script body—the PROJ version as well.
