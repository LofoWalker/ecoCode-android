package io.ecocode.java.checks.environment.release;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import java.util.List;

/**
 * Rule EREL008: Detects usage of non-WebP image formats in the {@code /res/drawable/} folder.
 * <p>
 * Using WebP instead of traditional formats such as {@code .png}, {@code .jpg}, {@code .jpeg} or {@code .gif}
 * in the {@code /res/drawable/} directory is a recommended practice. WebP is an image format developed by Google
 * that typically reduces image file size by approximately 30% compared to JPEG or PNG, without any noticeable
 * loss in quality.
 * </p>
 *
 * <p>
 * This rule flags all drawable resources using formats other than WebP under the {@code /res/drawable/} directory.
 * </p>
 *
 * <p>
 * Recommendation: Convert legacy image files in {@code /res/drawable/} to WebP to decrease APK size
 * and improve app performance. Android Studio provides built-in tools to facilitate the conversion.
 * </p>
 *
 * <p>
 * Targeted image formats:
 * {@code .png}, {@code .jpg}, {@code .jpeg}, {@code .gif}.
 * </p>
 *
 * @see <a href="https://developer.android.com/studio/write/image-format-webp">Android Developer Guide: WebP Format</a>
 */
@Rule(key = "EC535", name = "Convert to WebP", priority = Priority.MAJOR)
@DeprecatedRuleKey(repositoryKey = "ecoCode-java", ruleKey = "EREL008")
public class DrawableImageCompressionRule implements Sensor, JavaCheck {

    private static final String ERROR_MESSAGE = "Convert this image to WebP for better compression and smaller APK size. Non-WebP formats like PNG, JPG, or GIF are discouraged in /res/drawable/";
    private static final RuleKey RULE_KEY = RuleKey.of("ecoCode-android", "EREL008");
    private static final List<String> IMAGE_EXTENSIONS = List.of("png", "jpg", "jpeg", "gif");
    private static final String BASE_PATTERN = "**/res/drawable/**/*.%s";
    public static final String SENSOR_DESCRIPTION = "Detect non-WebP drawable images";

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(SENSOR_DESCRIPTION);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        FilePredicate predicate = fs.predicates()
                .matchesPathPatterns(getMatchingPathPatterns());

        fs.inputFiles(predicate)
                .forEach(inputFile -> insertIssue(context, inputFile));
    }

    private void insertIssue(SensorContext context, InputFile file) {
        NewIssue issue = context.newIssue().forRule(RULE_KEY);
        NewIssueLocation location = issue.newLocation()
                .on(file)
                .message(ERROR_MESSAGE);

        issue.at(location).save();
    }

    private String[] getMatchingPathPatterns() {
        return IMAGE_EXTENSIONS.stream()
                .map(ext -> String.format(BASE_PATTERN, ext))
                .toArray(String[]::new);
    }
}
