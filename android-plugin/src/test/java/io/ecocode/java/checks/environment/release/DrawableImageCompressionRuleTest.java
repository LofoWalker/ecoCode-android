package io.ecocode.java.checks.environment.release;

import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.rule.RuleKey;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DrawableImageCompressionRuleTest {

    @Test
    public void should_report_issue_on_non_webp_drawables() {
        SensorContextTester context = SensorContextTester.create(Paths.get("."));
        context.setFileSystem(context.fileSystem());


        List<String> imagesPath = List.of(
                "src/test/resources/project/res/drawable/logo.png",
                "src/test/resources/project/res/drawable/logo.jpg",
                "src/test/resources/project/res/drawable/logo.jpeg",
                "src/test/resources/project/res/drawable/logo.webp",
                "src/test/resources/project/res/drawable/logo.gif",
                "src/test/resources/project/res/drawable/logo.avi",
                "src/test/resources/project/res/drawable/logo.txt",
                "src/test/resources/project/res/drawable/logo.ico",
                "src/test/resources/project/res/drawable/logo.fav",
                "src/test/resources/project/res/drawable/logo.truc");

        addFilesToFileSystem(context, imagesPath);

        DrawableImageCompressionRule sensor = new DrawableImageCompressionRule();

        sensor.execute(context);
        assertThat(context.allIssues())
                .hasSize(4)
                .anyMatch(issue -> issue.ruleKey().equals(RuleKey.of("ecoCode-android", "EREL008")));
    }

    @Test
    public void should_not_report_issue_on_webp_or_irrelevant_files() {
        SensorContextTester context = SensorContextTester.create(Paths.get("."));
        context.setFileSystem(context.fileSystem());

        List<String> files = List.of(
                "res/drawable/logo.webp",
                "res/drawable/readme.txt",
                "res/values/strings.xml"
        );

        addFilesToFileSystem(context, files);

        new DrawableImageCompressionRule().execute(context);

        assertThat(context.allIssues()).isEmpty();
    }

   public void addFilesToFileSystem(SensorContextTester sensorContextTester, List<String> files) {
        files.stream()
                .map(DrawableImageCompressionRuleTest::createTestInputFile)
                .forEach(inputFile -> sensorContextTester.fileSystem().add(inputFile));
   }
    public static InputFile createTestInputFile(String relativePath) {
        return TestInputFileBuilder.create("myProject", relativePath)
                .build();
    }
}