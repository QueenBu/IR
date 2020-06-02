package execution;

import ARSystem.Argument;
import ARSystem.Ranker;
import tools.CLIHandler;
import tools.HttpService;

import java.util.List;

public class Manager {
    private CLIHandler cli;
    private HttpService httpService;


    public void start() {
        cli = new CLIHandler();
        httpService = new HttpService();

        while ( true ) {
            String query = cli.readUserInput("Please enter the phrase to be searched for! (empty input to cancel)");
            if (query.isEmpty()){
                break;
            }
            List<Argument> rankedArguments = Ranker.rank(httpService.getArguments(query));
            for ( int index = 0; index < rankedArguments.size() && index < 10; index++ ) {
                cli.writeToCL(rankedArguments.get(index));
            }
        }
    }

}
