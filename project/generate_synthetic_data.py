import argparse
from dataclasses import dataclass
import pathlib
import pandas as pd
from sdv.single_table import CTGANSynthesizer, CopulaGANSynthesizer
from sdv.evaluation.single_table import run_diagnostic, evaluate_quality
from sdv.datasets.local import load_csvs
import os
from sdv.metadata import SingleTableMetadata
from constants import ClassMetricVectorConstants, get_metric_columns, get_label_column
import logging

@dataclass
class Args:
    csv_input_path: str
    csv_output_path: str
    sample_size: int
    top_k_design_patterns: int

    @staticmethod
    def default():
        return Args(
            csv_input_path='/home/memi/Dokumente/master_thesis/project/metrics.csv',
            csv_output_path='/home/memi/Dokumente/master_thesis/project/syn.csv',
            sample_size=100,
            top_k_design_patterns=5
        )


def getArgs() -> Args:
    parser = argparse.ArgumentParser()
    parser.add_argument('--csvInputPath', '-i', required=True, type=str, dest='csv_input_path')
    parser.add_argument('--csvOutputPath', '-o', required=True, type=str, dest='csv_output_path')
    parser.add_argument('--sampleSize', '-s', type=int, dest='sample_size', default=2500)
    parser.add_argument('--topKDesignPattern', '-k', type=int, dest='top_k_design_patterns')
    args = parser.parse_args()

    return Args(
        csv_input_path=pathlib.Path(args.csv_input_path).absolute(),
        csv_output_path=pathlib.Path(args.csv_output_path).absolute(),
        sample_size=args.sample_size,
        top_k_design_patterns=args.top_k_design_patterns
    )


def get_top_k_design_patterns(df: pd.DataFrame, args: Args):
    df_grouped_by_design_pattern = df.drop_duplicates(
        [ClassMetricVectorConstants.MICRO_ARCHITECTURE, ClassMetricVectorConstants.DESIGN_PATTERN])
    df_grouped_by_design_pattern = df_grouped_by_design_pattern[ClassMetricVectorConstants.DESIGN_PATTERN].value_counts(
    ).sort_values(ascending=False).head(args.top_k_design_patterns)
    return df_grouped_by_design_pattern.index.to_list()

def report_results(df_actual: pd.DataFrame, df_synthetic: pd.DataFrame, metadata: SingleTableMetadata):
    diagnostic_report = run_diagnostic(df_actual, df_synthetic, metadata)
    quailty_report = evaluate_quality(df_actual, df_synthetic, metadata)
    
    logging.info(diagnostic_report.report_info)
    logging.info(quailty_report.report_info)

def generate_data(args: Args):
    logging.info(f'Reading read data form {args.csv_input_path}')
    df = pd.read_csv(args.csv_input_path)
    most_common_design_pattern = get_top_k_design_patterns(df, args)
    df = df[df[ClassMetricVectorConstants.DESIGN_PATTERN].isin(most_common_design_pattern)]
    df = df[get_label_column() + get_metric_columns()]
    metadata = SingleTableMetadata()
    metadata.detect_from_dataframe(df)
    logging.info('Starting fitting model')
    synthezier = CTGANSynthesizer(metadata)
    synthezier.fit(df)

    logging.info(f'Generating synthetic data with sample size {args.sample_size}')
    sampled_data = synthezier.sample(args.sample_size)
    report_results(df, sampled_data, metadata)


    logging.info(f'Writing synthetic data to {args.csv_output_path}')
    sampled_data.to_csv(args.csv_output_path, index=False)


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    args = getArgs()
    generate_data(args)
    