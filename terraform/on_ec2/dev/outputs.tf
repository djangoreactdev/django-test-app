
# output "prod_public_subnet_ids" {
#   value = module.vpc-prod.public_subnet_ids
# }

# output "prod_private_subnet_ids" {
#   value = module.vpc-prod.private_subnet_ids
# }

# output "dev_public_subnet_ids" {
#   value = module.vpc_module.public_subnet_ids
# }

# output "dev_private_subnet_ids" {
#   value = module.vpc_module.private_subnet_ids
# }

output "ec2_public_ip" {
  value = module.ec2_module.ec2_public_ip
}
