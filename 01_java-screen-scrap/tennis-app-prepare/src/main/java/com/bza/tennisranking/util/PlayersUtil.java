package com.bza.tennisranking.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bza.tennisranking.data.Player;
import com.bza.tennisranking.data.RankingHistory;
import com.bza.tennisranking.data.TennisMatch;
import com.bza.tennisranking.repository.PlayerRepository;
import com.bza.tennisranking.repository.RankingHistoryRepository;

public class PlayersUtil {
	// constant definition see explantion of Swisstennis in file Interpolation.xlsx
	// average values. These are not constants --- those values should be calculated in the future
	private static final double AVG_R8 = 1.23;
	private static final double AVG_R7 = 1.23;
	private static final double AVG_R6 = 1.23;
	private static final double AVG_R5 = 1.23;
	private static final double AVG_R4 = 1.23;
	private static final double AVG_R3 = 1.23;
	private static final double AVG_R2 = 1.23;
	private static final double AVG_R1 = 9.213;
	private static final double AVG_N4 = 1.23;
	private static final double AVG_N3 = 1.23;
	private static final double AVG_N2 = 1.23;
	private static final double AVG_N1 = 1.23;

	public static void storePlayers(PlayerRepository playerRepository,
			RankingHistoryRepository rankingHistoryRepository, Set<Player> players) {
		Set<RankingHistory> rhSet = new HashSet<RankingHistory>();
		Set<Player> pSet = new HashSet<Player>();
		Map<Integer, Player> playersMap = playerRepository.findAll().stream()
				.collect(Collectors.toMap(Player::getSwisstennisId, e -> e));
		System.out.println("Number of Players to store: " + players.size());
		int count = 0;
		for (Player p : players) {
			Player p2 = playersMap.get(p.getSwisstennisId());
			if ((++count % 1000) == 0)
				System.out.println("counter: " + count);
			if (p2 != null) {
				// player is already in the repository
				if (PlayersUtil.changed(p2, p)) {
					System.out.println("changed: " + PlayersUtil.changed(p2, p));
					p2.setCompValue(p.getCompValue());
					p2.setGradingValue(p.getGradingValue());
					p2.setRanking(p.getRanking());
					p2.setRankingNumber(p.getRankingNumber());
					p2.setCurrentPeriode(p.getCurrentPeriode());
					RankingHistory rh = new RankingHistory(p2, p.getRankingNumber(), p.getRanking(),
							p.getGradingValue(), p.getCompValue(), p.getCurrentPeriode());
					// playerRepository.save(p2);
					pSet.add(p2);
					rhSet.add(rh);
				}

			} else {
				RankingHistory rh = new RankingHistory(p, p.getRankingNumber(), p.getRanking(), p.getGradingValue(),
						p.getCompValue(), p.getCurrentPeriode());
				// playerRepository.save(p);
				pSet.add(p);
				rhSet.add(rh);
			}

		}
		playerRepository.save(pSet);
		rankingHistoryRepository.save(rhSet);
	}

	public static boolean changed(Player p1, Player p2) {
		int semesterA = Integer.parseInt(p1.getCurrentPeriode().split("/")[0]);
		int semesterB = Integer.parseInt(p2.getCurrentPeriode().split("/")[0]);
		int yearA = Integer.parseInt(p1.getCurrentPeriode().split("/")[1]);
		int yearB = Integer.parseInt(p2.getCurrentPeriode().split("/")[1]);
		if (yearA < yearB)
			return true;
		if (yearA > yearB)
			return false;
		return (semesterA < semesterB);
	}

	// calculates the competition value according to swisstennis formula
	// parameters given:
	// interpolValue (see the Excel Sheet from Swisstennis for an explanation.
	public static Double getNewCompValue(int swtId, Map<Integer, String> playersCompValue, Set<TennisMatch> matches,
			String interpolValue) {
		// retrieve the swisstennisid from the beaten players
		List<Integer> wonPlayerIds = matches.stream().filter(m -> m.getPlayer1Id() == swtId).map(m -> m.getPlayer2Id())
				.collect(Collectors.toList());
		List<String> compValueWonPlayers = wonPlayerIds.stream().map(id -> playersCompValue.get(id))
				.collect(Collectors.toList());

		// retrieve the list of the players we lost against
		List<Integer> lostPlayerIds = matches.stream().filter(m -> m.getPlayer2Id() == swtId).map(m -> m.getPlayer1Id())
				.collect(Collectors.toList());

		List<String> compValueLostPlayers = lostPlayerIds.stream().map(id -> playersCompValue.get(id))
				.collect(Collectors.toList());

		// System.out.println("CompValueWonPlayers: " + compValueWonPlayers);
		// System.out.println("compValueLostPlayers: " + compValueLostPlayers);

		// compute the number of Streichresultate
		int matchCounter = matches.size();
		int nbrStrResult = ((int) Math.floor(matchCounter / 6) > 3) ? 4 : (int) Math.floor(matchCounter / 6);
		nbrStrResult = (compValueLostPlayers.size() > nbrStrResult) ? nbrStrResult : compValueLostPlayers.size();

		// sort the list of lost matches
		compValueLostPlayers = compValueLostPlayers.stream()
				.sorted((n1, n2) -> Double.compare(Double.parseDouble(n2), Double.parseDouble(n1)))
				.collect(Collectors.toList());
		// cancel Streichresultate
		compValueLostPlayers = compValueLostPlayers.stream().limit(compValueLostPlayers.size() - nbrStrResult)
				.collect(Collectors.toList());

		return calculateNewCompValue(interpolValue, compValueWonPlayers, compValueLostPlayers);
	}

	// Map<Integer, RankingHistory > playersMap
	// calculates a run for a player given the competition values in the
	// playersMap
	/*
	 * public static List<Double> getNewRankingValues(int swisstennisId,
	 * Map<Integer, RankingHistory > playersMap, Set<TennisMatch> matches) {
	 * //Player player = players.stream().filter(p -> p.getSwisstennisId() ==
	 * swisstennisId). // findFirst().orElse(null);
	 * 
	 * // retrieve the swisstennisid from the beaten players List<Integer>
	 * wonPlayerIds = matches.stream().filter(m -> m.getPlayer1Id() ==
	 * swisstennisId) .map(m -> m.getPlayer2Id()).collect(Collectors.toList());
	 * //System.out.println("Wonid: " + wonPlayerIds);
	 * 
	 * // retrieve the competition value of those players List<String>
	 * compValueWonPlayers = wonPlayerIds.stream().map(id -> { Player playerWon
	 * = players.stream().filter(p -> p.getSwisstennisId() == id).
	 * findFirst().orElse(null); return playerWon.getCompValue();
	 * }).collect(Collectors.toList());
	 * 
	 * // retrieve the list of the players we lost against List<Integer>
	 * lostPlayerIds = matches.stream().filter(m -> m.getPlayer2Id() ==
	 * swisstennisId) .map(m -> m.getPlayer1Id()).collect(Collectors.toList());
	 * //System.out.println("Lostids: " + lostPlayerIds);
	 * 
	 * List<String> compValueLostPlayers = lostPlayerIds.stream().map(id -> {
	 * Player playerLost = players.stream().filter(p -> p.getSwisstennisId() ==
	 * id). findFirst().orElse(null); return playerLost.getCompValue();
	 * }).collect(Collectors.toList());
	 * 
	 * // compute the number of Streichresultate int matchCounter =
	 * wonPlayerIds.size() + lostPlayerIds.size(); int nbrStrResult = ((int)
	 * Math.floor(matchCounter/6) > 3) ? 4 : (int) Math.floor(matchCounter/6);
	 * nbrStrResult = (compValueLostPlayers.size() > nbrStrResult) ?
	 * nbrStrResult : compValueLostPlayers.size();
	 * 
	 * // sort the list of lost matches compValueLostPlayers =
	 * compValueLostPlayers.stream().sorted((n1,n2) ->
	 * Double.compare(Double.parseDouble(n2),
	 * Double.parseDouble(n1))).collect(Collectors.toList()); // cancel
	 * Streichresultate compValueLostPlayers = compValueLostPlayers.stream()
	 * .limit(compValueLostPlayers.size() - nbrStrResult)
	 * .collect(Collectors.toList());
	 * 
	 * //System.out.println("compValueLostPlayers: " + compValueLostPlayers);
	 * List<Double> pair = Arrays.asList(
	 * calculateNewCompValue(player.getCompValue(), compValueWonPlayers,
	 * compValueLostPlayers), calculateNewRiskValue(player.getCompValue(),
	 * compValueWonPlayers, compValueLostPlayers)); return pair;
	 * 
	 * }
	 */

	public static double calculateNewCompValue(String interpolValue, List<String> compValueWonPlayers,
			List<String> compValueLostPlayers) {
		double sumWinning = Math.exp(Double.parseDouble(interpolValue));
		for (String cV : compValueWonPlayers) {
			sumWinning = sumWinning + Math.exp(Double.parseDouble(cV));
		}

		double sumLosing = Math.exp(Double.parseDouble(interpolValue) * (-1));
		for (String cV : compValueLostPlayers) {
			sumLosing = sumLosing + Math.exp(Double.parseDouble(cV) * (-1));
		}

		System.out.println("sumWinning: " + sumWinning);
		System.out.println("sumlosing: " + sumLosing);
		double pos = Math.log(sumWinning);
		double neg = Math.log(sumLosing);

		double value = (pos - neg) / 2;
		return value;
	}

	// has to be redone
	public static double calculateNewRiskValue(String compValue, List<String> compValueWonPlayers,
			List<String> compValueLostPlayers) {
		double ownWinnerValue = Math.exp(Double.parseDouble(compValue));
		double sumWinning = 0;
		for (String cV : compValueWonPlayers) {
			sumWinning = sumWinning + Math.exp(Double.parseDouble(cV));
		}

		double sumLosing = 0;
		for (String cV : compValueLostPlayers) {
			sumLosing = sumLosing + Math.exp(Double.parseDouble(cV) * (-1));
		}

		// System.out.println("sumlosing: " + sumLosing);
		double pos = Math.log(sumWinning + Math.exp(Double.parseDouble(compValue)));
		double neg = Math.log(sumLosing + Math.exp(Double.parseDouble(compValue) * (-1)));

		double value = (pos + neg) / 6;
		return value;
	}

	// returns the previous periode e.g "1/2018 => 2/2017 or 2/2017 => 1/2017
	public static String previousPeriode(String periode) {
		int semester = Integer.parseInt(periode.split("/")[0]);
		int year = Integer.parseInt(periode.split("/")[1]);
		if (semester == 2) {
			return "1/" + year;
		}
		return "2/" + (year - 1);
	}

}
