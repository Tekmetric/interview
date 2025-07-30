#!/usr/bin/env python3
"""
NASA Near Earth Object Data Processor (Distributed Version)

This is the distributed entry point for the NEO data processor that uses
Spark for both data extraction and data processing, eliminating all 
single-threaded bottlenecks.

Key improvements over the original version:
- Distributed API calls using Spark parallelization
- Distributed data processing and joins using Spark DataFrames
- Comprehensive distributed aggregations
- Performance benchmarking capabilities
"""

import sys
import logging
import time
from pathlib import Path

# Add src directory to path so we can import our modules
sys.path.insert(0, str(Path(__file__).parent / "src"))

from src.distributed_neo_processor import process_neo_data_distributed, DistributedNEOPipeline
from src.neo_processor import process_neo_data  # Original for comparison


def main():
    """Main entry point demonstrating distributed processing capabilities"""
    
    print("🚀 NASA Near Earth Object Data Processor (DISTRIBUTED Version)")
    print("="*80)
    print("This version demonstrates:")
    print("✅ DISTRIBUTED data extraction using Spark parallelization")
    print("✅ DISTRIBUTED data processing with Spark DataFrames")
    print("✅ DISTRIBUTED joins and aggregations")
    print("✅ Comprehensive performance metrics")
    print("✅ Real-time processing speed monitoring")
    print("✅ Automatic optimal parallelism detection")
    print("="*80)
    
    # Configure logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    try:
        # Option 1: Run distributed processing
        print("\n🎯 Running DISTRIBUTED processing pipeline...")
        print("-" * 60)
        
        distributed_start = time.time()
        distributed_result = process_neo_data_distributed(
            limit=100,  # Increased limit to show parallelization benefits
            parallelism=4  # Use 4 parallel partitions for API calls
        )
        distributed_end = time.time()
        distributed_time = distributed_end - distributed_start
        
        print(f"\n✅ DISTRIBUTED Processing Results:")
        print(f"📊 Objects processed: {distributed_result.total_objects_processed}")
        print(f"⏱️ Processing time: {distributed_time:.2f} seconds")
        print(f"⚡ Processing speed: {distributed_result.total_objects_processed/distributed_time:.1f} objects/second")
        print(f"🎯 Success rate: {(distributed_result.successful_records/distributed_result.total_objects_processed)*100:.1f}%")
        print(f"📁 Output files: {len(distributed_result.output_paths)}")
        
        # Show aggregation results
        agg = distributed_result.aggregations
        print(f"\n📈 Enhanced Aggregations:")
        print(f"  • Close approaches < 0.2 AU: {agg['close_approaches_under_02_au']}")
        print(f"  • Total objects processed: {agg['total_objects_processed']}")
        print(f"  • Approaches by year: {agg['approaches_by_year']}")
        
        # Show enhanced statistics if available
        if 'velocity_statistics' in agg:
            vel_stats = agg['velocity_statistics']
            print(f"  • Velocity stats: avg={vel_stats['avg']:.1f} km/s, max={vel_stats['max']:.1f} km/s")
        
        if 'distance_statistics' in agg:
            dist_stats = agg['distance_statistics']
            print(f"  • Distance stats: avg={dist_stats['avg_au']:.3f} AU, min={dist_stats['min_au']:.3f} AU")
        
        if 'hazard_distribution' in agg:
            hazard_dist = agg['hazard_distribution']
            print(f"  • Hazard distribution: {hazard_dist}")
        
        # Option 2: Performance comparison (if time permits)
        print(f"\n🏁 Performance Comparison Available:")
        print(f"Run with --compare flag to see distributed vs single-threaded performance")
        
        # Show output structure
        print(f"\n📂 Generated data structure:")
        for name, path in distributed_result.output_paths.items():
            if path:
                print(f"  • {name}: {path}")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ Error: {e}")
        logging.error(f"Distributed pipeline failed: {e}", exc_info=True)
        return 1


def run_performance_comparison():
    """Run performance comparison between single-threaded and distributed processing"""
    
    print("\n🏁 Performance Comparison: Single-threaded vs Distributed")
    print("="*80)
    
    test_size = 50  # Smaller size for quick comparison
    
    try:
        # Test 1: Original single-threaded approach
        print("🔄 Testing original single-threaded approach...")
        single_start = time.time()
        single_result = process_neo_data(limit=test_size)
        single_end = time.time()
        single_time = single_end - single_start
        
        # Test 2: Distributed approach
        print("⚡ Testing distributed approach...")
        distributed_start = time.time()
        distributed_result = process_neo_data_distributed(
            limit=test_size,
            parallelism=4
        )
        distributed_end = time.time()
        distributed_time = distributed_end - distributed_start
        
        # Performance comparison
        print(f"\n📊 Performance Comparison Results:")
        print(f"{'Metric':<30} {'Single-threaded':<20} {'Distributed':<20} {'Improvement':<15}")
        print("-" * 85)
        
        print(f"{'Processing time':<30} {single_time:.2f}s {distributed_time:.2f}s"
              f" {((single_time - distributed_time) / single_time * 100):+.1f}%")
        
        single_speed = test_size / single_time
        distributed_speed = test_size / distributed_time
        print(f"{'Objects per second':<30} {single_speed:.1f} {distributed_speed:.1f}"
              f" {((distributed_speed - single_speed) / single_speed * 100):+.1f}%")
        
        print(f"{'Objects processed':<30} {single_result.total_objects_processed} "
              f"{distributed_result.total_objects_processed} "
              f"{'Same' if single_result.total_objects_processed == distributed_result.total_objects_processed else 'Different'}")
        
        print(f"{'Aggregations calculated':<30} {len(single_result.aggregations)} "
              f"{len(distributed_result.aggregations)} "
              f"{len(distributed_result.aggregations) - len(single_result.aggregations):+d}")
        
        # Show speedup factor
        speedup = single_time / distributed_time if distributed_time > 0 else 0
        print(f"\n⚡ Speedup Factor: {speedup:.2f}x")
        
        if speedup > 1:
            print(f"✅ Distributed processing is {speedup:.2f}x faster!")
        elif speedup < 1:
            print(f"⚠️ Single-threaded is {1/speedup:.2f}x faster (overhead for small datasets)")
        else:
            print("🤝 Performance is similar")
            
        return 0
        
    except Exception as e:
        print(f"❌ Comparison failed: {e}")
        return 1


def run_benchmark():
    """Run comprehensive performance benchmark"""
    
    print("\n🏁 Comprehensive Performance Benchmark")
    print("="*80)
    
    try:
        processor = DistributedNEOPipeline()
        results = processor.run_performance_benchmark([25, 50, 100])
        
        print("\n📊 Benchmark Results:")
        print(f"{'Size':<10} {'Time (s)':<12} {'Speed (obj/s)':<15} {'Parallelism':<12} {'Success %':<10}")
        print("-" * 65)
        
        for size, metrics in results.items():
            if "error" not in metrics:
                print(f"{size:<10} {metrics['processing_time_seconds']:<12.2f} "
                      f"{metrics['objects_per_second']:<15.1f} "
                      f"{metrics['parallelism_used']:<12} "
                      f"{metrics['success_rate']:<10.1f}")
            else:
                print(f"{size:<10} ERROR: {metrics['error']}")
        
        return 0
        
    except Exception as e:
        print(f"❌ Benchmark failed: {e}")
        return 1


if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description="Distributed NASA NEO Data Processor")
    parser.add_argument("--compare", action="store_true", 
                        help="Run performance comparison with single-threaded version")
    parser.add_argument("--benchmark", action="store_true", 
                        help="Run comprehensive performance benchmark")
    parser.add_argument("--verbose", "-v", action="store_true", 
                        help="Enable verbose logging")
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    if args.compare:
        exit(run_performance_comparison())
    elif args.benchmark:
        exit(run_benchmark())
    else:
        exit(main()) 