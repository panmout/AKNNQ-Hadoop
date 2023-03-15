package gr.uth.ece.dsel.aknn_hadoop.phase4;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public final class Mapper4 extends Mapper<Object, Text, IntWritable, Text>
{
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException
	{
		final String line = value.toString();
		final String[] data = line.trim().split("\t");

		final int outKey = Integer.parseInt(data[0]); // qpoint_id

		final StringBuilder outValue = new StringBuilder();
		
		if (data[data.length - 1].equals("true")) // MR3 output 'true', pass it through
			outValue.append("true");
		else if (data[data.length - 1].equals("false")) // MR3 output 'false'
		{
			// if there is no z (2d) then MR3 'false' output contains 5, 7, 9,... elements
			// else (3d) it contains 6, 8,... elements
			// in 2d the neighbors list starts at index 4, in 3d it starts at 5 
			final int c = data.length % 2 != 0 ? 4 : 5;
			
			for (int i = c; i < data.length; i++) // reading neighbor list
				outValue.append(String.format("%s\t", data[i]));
		}
		else // MR2 output
		{
			// if there is no z (2d) then MR2 output contains 4, 6, 8,... elements
			// else (3d) it contains 5, 7, 9,... elements
			// in 2d the neighbors list starts at index 4, in 3d it starts at 5 
			final int c = data.length % 2 == 0 ? 4 : 5;
			
			// if there is a knn list for 2d or 3d, read it
			if (data.length > c)
				for (int i = c; i < data.length; i++)
					outValue.append(String.format("%s\t", data[i]));
			
			outValue.append("false"); // finally append "false"
		}
		context.write(new IntWritable(outKey), new Text(outValue.toString()));
	}
}
