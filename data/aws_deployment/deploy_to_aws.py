# deploy_to_aws.py - Updated version that creates log group
import argparse
import subprocess
import json
import time
import os

def run_command(cmd, description=None):
    """Run a shell command and return the output"""
    if description:
        print(f"\n>>> {description}")
    
    print(f"Running: {' '.join(cmd)}")
    result = subprocess.run(cmd, capture_output=True, text=True)
    
    if result.returncode != 0:
        print(f"Error: {result.stderr}")
        return None
    
    return result.stdout.strip()

def parse_args():
    """Parse command line arguments"""
    parser = argparse.ArgumentParser(description='Deploy existing container to AWS')
    parser.add_argument('--region', type=str, default='us-west-2', help='AWS region')
    parser.add_argument('--service-name', type=str, default='neo-predictor', help='Service name')
    parser.add_argument('--fargate-cpu', type=str, default='1024', help='CPU units (1024=1 vCPU)')
    parser.add_argument('--fargate-memory', type=str, default='2048', help='Memory in MB')
    return parser.parse_args()

def main():
    """Main deployment function"""
    args = parse_args()
    
    # Use the existing repository and URI
    repo_name = "neo-predictor-repo"
    repo_uri = f"<YOUR_AWS_ACCOUNT_ID>.dkr.ecr.{args.region}.amazonaws.com/{repo_name}"
    
    print(f"Starting deployment to AWS ECS (Fargate) in region {args.region}...")
    print(f"Using repository: {repo_uri}")
    
    # Step 0: Create CloudWatch Log Group
    log_group_name = f"/ecs/{args.service_name}"
    print(f"\n>>> Creating CloudWatch log group: {log_group_name}")
    
    log_group_result = run_command([
        'aws', 'logs', 'create-log-group',
        '--log-group-name', log_group_name,
        '--region', args.region
    ])
    
    # It's okay if the log group already exists
    print(f"Log group created or already exists: {log_group_name}")
    
    # Step 1: Create a task definition
    print(f"\n>>> Creating ECS task definition")
    
    # Create a task definition file
    task_def = {
        "family": f"{args.service_name}-task",
        "networkMode": "awsvpc",
        "executionRoleArn": f"arn:aws:iam::<YOUR_AWS_ACCOUNT_ID>:role/ecsTaskExecutionRole",
        "requiresCompatibilities": ["FARGATE"],
        "cpu": args.fargate_cpu,
        "memory": args.fargate_memory,
        "containerDefinitions": [
            {
                "name": args.service_name,
                "image": f"{repo_uri}:latest",
                "essential": True,
                "portMappings": [
                    {
                        "containerPort": 5000,
                        "hostPort": 5000,
                        "protocol": "tcp"
                    }
                ],
                "logConfiguration": {
                    "logDriver": "awslogs",
                    "options": {
                        "awslogs-group": log_group_name,
                        "awslogs-region": args.region,
                        "awslogs-stream-prefix": "ecs"
                    }
                }
            }
        ]
    }
    
    # Write task definition to file
    with open("task-definition.json", "w") as f:
        json.dump(task_def, f, indent=2)
    
    # Register task definition
    task_def_result = run_command(['aws', 'ecs', 'register-task-definition', 
                                  '--cli-input-json', 'file://task-definition.json', 
                                  '--region', args.region])
    if not task_def_result:
        print("Failed to register task definition. Check if the ecsTaskExecutionRole exists.")
        return
    
    task_def_json = json.loads(task_def_result)
    task_def_arn = task_def_json['taskDefinition']['taskDefinitionArn']
    print(f"Created task definition: {task_def_arn}")
    
    # Step 2: Create a cluster if it doesn't exist
    print(f"\n>>> Creating ECS cluster")
    cluster_name = f"{args.service_name}-cluster"
    cluster_result = run_command(['aws', 'ecs', 'create-cluster', 
                                 '--cluster-name', cluster_name, 
                                 '--region', args.region])
    if not cluster_result:
        print("Failed to create cluster.")
        return
    
    cluster_json = json.loads(cluster_result)
    cluster_arn = cluster_json['cluster']['clusterArn']
    print(f"Created/using cluster: {cluster_arn}")
    
    # Step 3: Create security group for the service
    print(f"\n>>> Creating/finding security group")
    vpc_result = run_command(['aws', 'ec2', 'describe-vpcs', 
                             '--filters', 'Name=isDefault,Values=true', 
                             '--query', 'Vpcs[0].VpcId', 
                             '--output', 'text',
                             '--region', args.region])
    if not vpc_result:
        print("Failed to get default VPC.")
        return
    
    vpc_id = vpc_result
    print(f"Using VPC: {vpc_id}")
    
    # Check if security group already exists
    sg_name = f"{args.service_name}-sg"
    sg_check = run_command(['aws', 'ec2', 'describe-security-groups',
                           '--filters', f"Name=group-name,Values={sg_name}", f"Name=vpc-id,Values={vpc_id}",
                           '--query', 'SecurityGroups[0].GroupId',
                           '--output', 'text',
                           '--region', args.region])
    
    if sg_check and sg_check != 'None':
        sg_id = sg_check
        print(f"Using existing security group: {sg_id}")
    else:
        # Create security group
        sg_result = run_command(['aws', 'ec2', 'create-security-group', 
                                '--group-name', sg_name, 
                                '--description', f"Security group for {args.service_name}", 
                                '--vpc-id', vpc_id,
                                '--region', args.region])
        if not sg_result:
            print("Failed to create security group.")
            return
        
        sg_json = json.loads(sg_result)
        sg_id = sg_json['GroupId']
        print(f"Created security group: {sg_id}")
    
    # Add inbound rule for the API port
    print(f"\n>>> Adding inbound rule to security group")
    inbound_result = run_command(['aws', 'ec2', 'authorize-security-group-ingress', 
                                 '--group-id', sg_id, 
                                 '--protocol', 'tcp', 
                                 '--port', '5000', 
                                 '--cidr', '0.0.0.0/0',
                                 '--region', args.region])
    
    # This may fail if the rule already exists, which is fine
    if not inbound_result:
        print("Note: Inbound rule may already exist, continuing...")
    
    # Step 4: Get subnets for the default VPC
    print(f"\n>>> Finding subnets for VPC")
    subnets_result = run_command(['aws', 'ec2', 'describe-subnets', 
                                 '--filters', f"Name=vpc-id,Values={vpc_id}", 
                                 '--query', 'Subnets[*].SubnetId', 
                                 '--output', 'json',
                                 '--region', args.region])
    if not subnets_result:
        print("Failed to get subnets.")
        return
    
    subnets = json.loads(subnets_result)
    if len(subnets) < 2:
        print(f"Warning: Only found {len(subnets)} subnets. ECS service may require at least 2.")
    
    # Take up to 2 subnets
    subnet_list = subnets[:2]
    subnet_ids_str = ','.join([f'"{s}"' for s in subnet_list])
    print(f"Using subnets: {subnet_list}")
    
    # Step 5: Delete existing service if it exists
    print(f"\n>>> Checking for existing service")
    service_name = args.service_name
    service_exists = run_command(['aws', 'ecs', 'describe-services',
                                 '--cluster', cluster_name,
                                 '--services', service_name,
                                 '--region', args.region])
    
    if service_exists:
        service_json = json.loads(service_exists)
        if service_json.get('services') and len(service_json['services']) > 0 and service_json['services'][0]['status'] != 'INACTIVE':
            # Delete existing service
            print(f"Service {service_name} already exists, deleting it first...")
            delete_result = run_command(['aws', 'ecs', 'delete-service',
                                       '--cluster', cluster_name,
                                       '--service', service_name,
                                       '--force',
                                       '--region', args.region])
            
            print("Waiting for service to be deleted...")
            time.sleep(60)  # Give time for the service to be deleted
    
    # Step 6: Create the service
    print(f"\n>>> Creating ECS service")
    
    # Create the service
    network_config = f"awsvpcConfiguration={{subnets=[{subnet_ids_str}],securityGroups=[\"{sg_id}\"],assignPublicIp=ENABLED}}"
    
    service_cmd = [
        'aws', 'ecs', 'create-service',
        '--cluster', cluster_name,
        '--service-name', service_name,
        '--task-definition', task_def_arn,
        '--desired-count', '1',
        '--launch-type', 'FARGATE',
        '--network-configuration', network_config,
        '--region', args.region
    ]
    
    service_result = run_command(service_cmd)
    if not service_result:
        print("Failed to create service.")
        return
    
    service_json = json.loads(service_result)
    service_arn = service_json['service']['serviceArn']
    print(f"Created service: {service_arn}")
    
    # Step 7: Save deployment info
    deployment_info = {
        'repository_uri': repo_uri,
        'cluster_name': cluster_name,
        'service_name': service_name,
        'task_definition': task_def_arn,
        'security_group': sg_id,
        'region': args.region,
        'api_endpoint_template': f"http://<public-ip>:5000 (Find the public IP in the AWS ECS console)",
        'command_to_test': "curl http://<public-ip>:5000/health"
    }
    
    with open('aws_deployment_info.json', 'w') as f:
        json.dump(deployment_info, f, indent=2)
    
    print("\nDeployment to AWS initiated!")
    print("It may take a few minutes for the service to start and be accessible.")
    print("\nTo find your service's public IP address:")
    print("1. Go to AWS Console → ECS → Clusters → neo-predictor-cluster")
    print("2. Click on the Tasks tab and find your running task")
    print("3. Click on the task to view details")
    print("4. Look for the Public IP field")
    print("\nYour API will be accessible at: http://<public-ip>:5000")
    print("Deployment information saved to aws_deployment_info.json")

if __name__ == "__main__":
    main()